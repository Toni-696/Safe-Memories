package com.toni.safememories.service;

import com.toni.safememories.dto.CarpetaRequest;
import com.toni.safememories.entity.Archivo;
import com.toni.safememories.entity.Carpeta;
import com.toni.safememories.entity.Usuario;
import com.toni.safememories.repository.ArchivoRepository;
import com.toni.safememories.repository.CarpetaRepository;
import com.toni.safememories.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarpetaService {

    private final CarpetaRepository carpetaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArchivoRepository archivoRepository;

    public CarpetaService(CarpetaRepository carpetaRepository,
                          UsuarioRepository usuarioRepository,
                          ArchivoRepository archivoRepository) {
        this.carpetaRepository = carpetaRepository;
        this.usuarioRepository = usuarioRepository;
        this.archivoRepository = archivoRepository;
    }

    public Carpeta crearCarpeta(CarpetaRequest request, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (carpetaRepository.existsByNombreAndUsuario(request.getNombre(), usuario)) {
            throw new RuntimeException("Ya existe una carpeta con ese nombre");
        }

        Carpeta carpeta = Carpeta.builder()
                .nombre(request.getNombre())
                .usuario(usuario)
                .build();

        return carpetaRepository.save(carpeta);
    }

    public List<Carpeta> obtenerMisCarpetas(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return carpetaRepository.findByUsuario(usuario);
    }

    public List<Archivo> obtenerArchivosDeCarpeta(Long carpetaId, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new RuntimeException("Carpeta no encontrada"));

        if (carpeta.getUsuario().getId() != usuario.getId()) {
            throw new RuntimeException("No tienes permiso para ver esta carpeta");
        }

        return archivoRepository.findByCarpeta(carpeta);
    }
        //UPDATE para cambiar el nombre de la carpeta
    public Carpeta renombrarCarpeta(Long carpetaId, CarpetaRequest request, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new RuntimeException("Carpeta no encontrada"));

        if (carpeta.getUsuario().getId() != usuario.getId()) {
            throw new RuntimeException("No tienes permiso para modificar esta carpeta");
        }

        if (carpetaRepository.existsByNombreAndUsuario(request.getNombre(), usuario)) {
            throw new RuntimeException("Ya existe una carpeta con ese nombre");
        }

        carpeta.setNombre(request.getNombre());

        return carpetaRepository.save(carpeta);
    }

    public void borrarCarpeta(Long carpetaId, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new RuntimeException("Carpeta no encontrada"));

        if (carpeta.getUsuario().getId() != usuario.getId()) {
            throw new RuntimeException("No tienes permiso para borrar esta carpeta");
        }

        List<Archivo> archivos = archivoRepository.findByCarpeta(carpeta);

        for (Archivo archivo : archivos) {
            archivo.setCarpeta(null);
        }

        archivoRepository.saveAll(archivos);

        carpetaRepository.delete(carpeta);
    }
}
