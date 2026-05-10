package com.toni.safememories.service;

import com.toni.safememories.dto.SolicitudDescargaRequest;
import com.toni.safememories.entity.*;
import com.toni.safememories.repository.ArchivoRepository;
import com.toni.safememories.repository.PermisoCarpetaRepository;
import com.toni.safememories.repository.PermisoDescargaRepository;
import com.toni.safememories.repository.SolicitudDescargaRepository;
import com.toni.safememories.repository.UsuarioRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class SolicitudDescargaService {

    private final SolicitudDescargaRepository solicitudDescargaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArchivoRepository archivoRepository;
    private final PermisoCarpetaRepository permisoCarpetaRepository;
    private final PermisoDescargaRepository permisoDescargaRepository;

    public SolicitudDescargaService(SolicitudDescargaRepository solicitudDescargaRepository,
                                    UsuarioRepository usuarioRepository,
                                    ArchivoRepository archivoRepository,
                                    PermisoCarpetaRepository permisoCarpetaRepository,
                                    PermisoDescargaRepository permisoDescargaRepository) {
        this.solicitudDescargaRepository = solicitudDescargaRepository;
        this.usuarioRepository = usuarioRepository;
        this.archivoRepository = archivoRepository;
        this.permisoCarpetaRepository = permisoCarpetaRepository;
        this.permisoDescargaRepository = permisoDescargaRepository;
    }

    public SolicitudDescarga crearSolicitud(String emailSolicitante, SolicitudDescargaRequest request) {
        Usuario solicitante = usuarioRepository.findByEmail(emailSolicitante)
                .orElseThrow(() -> new RuntimeException("Usuario solicitante no encontrado"));

        if (request.getArchivosIds() == null || request.getArchivosIds().isEmpty()) {
            throw new RuntimeException("Debes seleccionar al menos un archivo");
        }

        List<Archivo> archivos = archivoRepository.findAllById(request.getArchivosIds());

        if (archivos.size() != request.getArchivosIds().size()) {
            throw new RuntimeException("Algún archivo no existe");
        }

        Usuario propietario = archivos.get(0).getUsuario();

        for (Archivo archivo : archivos) {
            if (archivo.getUsuario().getId() != propietario.getId()) {
                throw new RuntimeException("Todos los archivos deben pertenecer al mismo propietario");
            }

            if (archivo.getCarpeta() == null) {
                throw new RuntimeException("El archivo debe pertenecer a una carpeta compartida");
            }

            boolean tienePermisoCarpeta = permisoCarpetaRepository
                    .existsByCarpetaAndUsuarioAutorizado(archivo.getCarpeta(), solicitante);

            if (!tienePermisoCarpeta) {
                throw new RuntimeException("No tienes permiso para solicitar descarga de uno de los archivos");
            }
        }

        SolicitudDescarga solicitud = SolicitudDescarga.builder()
                .usuarioSolicitante(solicitante)
                .usuarioPropietario(propietario)
                .archivos(archivos)
                .build();

        return solicitudDescargaRepository.save(solicitud);
    }

    public List<SolicitudDescarga> obtenerSolicitudesRecibidas(String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return solicitudDescargaRepository.findByUsuarioPropietarioConArchivos(propietario);
    }

    public SolicitudDescarga aceptarSolicitud(Long solicitudId, String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        SolicitudDescarga solicitud = solicitudDescargaRepository.findByIdConArchivos(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getUsuarioPropietario().getId() != propietario.getId()) {
            throw new RuntimeException("No tienes permiso para aceptar esta solicitud");
        }

        solicitud.setEstado(EstadoSolicitud.ACEPTADA);

        for (Archivo archivo : solicitud.getArchivos()) {
            boolean yaTienePermiso = permisoDescargaRepository
                    .existsByArchivoAndUsuarioAutorizado(archivo, solicitud.getUsuarioSolicitante());

            if (!yaTienePermiso) {
                PermisoDescarga permiso = PermisoDescarga.builder()
                        .archivo(archivo)
                        .usuarioAutorizado(solicitud.getUsuarioSolicitante())
                        .build();

                permisoDescargaRepository.save(permiso);
            }
        }

        return solicitudDescargaRepository.save(solicitud);
    }

    public SolicitudDescarga rechazarSolicitud(Long solicitudId, String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        SolicitudDescarga solicitud = solicitudDescargaRepository.findByIdConArchivos(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getUsuarioPropietario().getId() != propietario.getId()) {
            throw new RuntimeException("No tienes permiso para rechazar esta solicitud");
        }

        solicitud.setEstado(EstadoSolicitud.RECHAZADA);

        return solicitudDescargaRepository.save(solicitud);
    }
}