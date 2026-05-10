package com.toni.safememories.service;

import com.toni.safememories.dto.ArchivoRequest;
import com.toni.safememories.entity.Archivo;
import com.toni.safememories.entity.Carpeta;
import com.toni.safememories.entity.PermisoDescarga;
import com.toni.safememories.entity.Usuario;
import com.toni.safememories.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class ArchivoService {

    private final ArchivoRepository archivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PermisoDescargaRepository permisoDescargaRepository;
    private final CarpetaRepository carpetaRepository;
    private final PermisoCarpetaRepository permisoCarpetaRepository;

    public ArchivoService(ArchivoRepository archivoRepository,
                          UsuarioRepository usuarioRepository,
                          PermisoDescargaRepository permisoDescargaRepository,
                          CarpetaRepository carpetaRepository,
                          PermisoCarpetaRepository permisoCarpetaRepository) {
        this.archivoRepository = archivoRepository;
        this.usuarioRepository = usuarioRepository;
        this.permisoDescargaRepository = permisoDescargaRepository;
        this.carpetaRepository = carpetaRepository;
        this.permisoCarpetaRepository = permisoCarpetaRepository;
    }

    public Archivo crearArchivo(ArchivoRequest request, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Archivo archivo = Archivo.builder()
                .nombreOriginal(request.getNombreOriginal())
                .nombreGuardado(request.getNombreGuardado())
                .ruta(request.getRuta())
                .tipo(request.getTipo())
                .tamano(request.getTamano())
                .usuario(usuario)
                .build();

        return archivoRepository.save(archivo);
    }

    public List<Archivo> obtenerArchivosDeUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return archivoRepository.findByUsuario(usuario);
    }

    public Archivo subirArchivo(MultipartFile archivo, String emailUsuario, Long carpetaId) throws Exception {
        if (archivo.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        String tipo = archivo.getContentType();
        long tamanoMaximo = 10 * 1024 * 1024; // 10 MB

        if (tipo == null || (!tipo.equals("image/jpeg")
                && !tipo.equals("image/png")
                && !tipo.equals("video/mp4"))) {
            throw new RuntimeException("Tipo de archivo no permitido. Solo se permiten JPG, PNG y MP4");
        }

        if (archivo.getSize() > tamanoMaximo) {
            throw new RuntimeException("El archivo supera el tamaño máximo permitido de 10 MB");
        }

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carpeta carpeta = null;

        if (carpetaId != null) {
            carpeta = carpetaRepository.findById(carpetaId)
                    .orElseThrow(() -> new RuntimeException("Carpeta no encontrada"));

            if (carpeta.getUsuario().getId() != usuario.getId()) {
                throw new RuntimeException("No tienes permiso para usar esta carpeta");
            }
        }

        String nombreOriginal = archivo.getOriginalFilename();
        Long tamano = archivo.getSize();

        String extension = "";

        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));//saco la extensión a partir del "."
        }

        String nombreGuardado = UUID.randomUUID().toString() + extension;//genera UUID aleatorio y le añade la extensión

        String carpetaUploads = System.getProperty("user.dir") + File.separator + "uploads";
        File directorioUploads = new File(carpetaUploads);

        if (!directorioUploads.exists()) {
            directorioUploads.mkdirs();
        }

        String rutaCompleta = carpetaUploads + File.separator + nombreGuardado;
        File destino = new File(rutaCompleta);

        archivo.transferTo(destino);

        String rutaGuardadaEnBd = "/uploads/" + nombreGuardado;

        Archivo nuevoArchivo = Archivo.builder()
                .nombreOriginal(nombreOriginal)
                .nombreGuardado(nombreGuardado)
                .ruta(rutaGuardadaEnBd)
                .tipo(tipo)
                .tamano(tamano)
                .usuario(usuario)
                .carpeta(carpeta)
                .build();

        return archivoRepository.save(nuevoArchivo);
    }

    public Archivo obtenerArchivoPorId(Long idArchivo, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Archivo archivo = archivoRepository.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        if (archivo.getUsuario().getId() != usuario.getId()) {
            throw new RuntimeException("No tienes permiso para acceder a este archivo");
        }

        return archivo;
    }

    public void borrarArchivo(Long idArchivo, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Archivo archivo = archivoRepository.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        if (archivo.getUsuario().getId() != usuario.getId()) {
            throw new RuntimeException("No tienes permiso para borrar este archivo");
        }

        String rutaArchivo = System.getProperty("user.dir")//contruye una ruta física real del archivo
                + File.separator
                + "uploads"
                + File.separator
                + archivo.getNombreGuardado();

        File fichero = new File(rutaArchivo);

        if (fichero.exists()) {
            fichero.delete();
        }

        archivoRepository.delete(archivo);
    }

    public void concederPermisoDescarga(Long idArchivo, String emailPropietario, String emailUsuarioAutorizado) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        Usuario usuarioAutorizado = usuarioRepository.findByEmail(emailUsuarioAutorizado)
                .orElseThrow(() -> new RuntimeException("Usuario autorizado no encontrado"));

        Archivo archivo = archivoRepository.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        if (archivo.getUsuario().getId() != propietario.getId()) {
            throw new RuntimeException("No tienes permiso para compartir este archivo");
        }

        if (permisoDescargaRepository.existsByArchivoAndUsuarioAutorizado(archivo, usuarioAutorizado)) {
            throw new RuntimeException("Este usuario ya tiene permiso para descargar el archivo");
        }

        PermisoDescarga permiso = PermisoDescarga.builder()
                .archivo(archivo)
                .usuarioAutorizado(usuarioAutorizado)
                .build();

        permisoDescargaRepository.save(permiso);
    }

    public Archivo obtenerArchivoParaDescarga(Long idArchivo, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Archivo archivo = archivoRepository.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        boolean esPropietario = archivo.getUsuario().getId() == usuario.getId();

        boolean tienePermiso = permisoDescargaRepository
                .existsByArchivoAndUsuarioAutorizado(archivo, usuario);

        if (!esPropietario && !tienePermiso) {
            throw new RuntimeException("No tienes permiso para descargar este archivo");
        }

        return archivo;
    }
    public List<Archivo> obtenerArchivosCompartidosConmigo(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<PermisoDescarga> permisos = permisoDescargaRepository.findByUsuarioAutorizado(usuario);

        return permisos.stream()
                .map(PermisoDescarga::getArchivo)
                .toList();
    }
    public void revocarPermisoDescarga(Long idArchivo, String emailPropietario, String emailUsuarioAutorizado) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        Usuario usuarioAutorizado = usuarioRepository.findByEmail(emailUsuarioAutorizado)
                .orElseThrow(() -> new RuntimeException("Usuario autorizado no encontrado"));

        Archivo archivo = archivoRepository.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        if (archivo.getUsuario().getId() != propietario.getId()) {
            throw new RuntimeException("No tienes permiso para modificar este archivo");
        }

        PermisoDescarga permiso = permisoDescargaRepository
                .findByArchivoAndUsuarioAutorizado(archivo, usuarioAutorizado)
                .orElseThrow(() -> new RuntimeException("Este usuario no tenía permiso sobre este archivo"));

        permisoDescargaRepository.delete(permiso);
    }
    //UPDATE
    public Archivo renombrarArchivo(Long idArchivo, String nuevoNombre, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Archivo archivo = archivoRepository.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        if (archivo.getUsuario().getId() != usuario.getId()) {
            throw new RuntimeException("No tienes permiso para modificar este archivo");
        }

        archivo.setNombreOriginal(nuevoNombre);

        return archivoRepository.save(archivo);
    }

    public Archivo moverArchivo(Long idArchivo, Long carpetaId, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Archivo archivo = archivoRepository.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        if (archivo.getUsuario().getId() != usuario.getId()) {
            throw new RuntimeException("No tienes permiso para mover este archivo");
        }

        Carpeta carpetaDestino = null;

        if (carpetaId != null) {
            carpetaDestino = carpetaRepository.findById(carpetaId)
                    .orElseThrow(() -> new RuntimeException("Carpeta destino no encontrada"));

            if (carpetaDestino.getUsuario().getId() != usuario.getId()) {
                throw new RuntimeException("No tienes permiso para mover el archivo a esta carpeta");
            }
        }

        archivo.setCarpeta(carpetaDestino);

        return archivoRepository.save(archivo);
    }
    public Archivo obtenerArchivoParaVisualizar(Long idArchivo, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Archivo archivo = archivoRepository.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

        boolean esPropietario = archivo.getUsuario().getId() == usuario.getId();

        boolean tienePermisoDescarga = permisoDescargaRepository
                .existsByArchivoAndUsuarioAutorizado(archivo, usuario);

        boolean tienePermisoCarpeta = false;

        if (archivo.getCarpeta() != null) {
            tienePermisoCarpeta = permisoCarpetaRepository
                    .existsByCarpetaAndUsuarioAutorizado(archivo.getCarpeta(), usuario);
        }

        if (!esPropietario && !tienePermisoDescarga && !tienePermisoCarpeta) {
            throw new RuntimeException("No tienes permiso para ver este archivo");
        }

        return archivo;
    }
}