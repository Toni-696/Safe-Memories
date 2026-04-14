package com.toni.safememories.service;

import com.toni.safememories.dto.ArchivoRequest;
import com.toni.safememories.entity.Archivo;
import com.toni.safememories.entity.Usuario;
import com.toni.safememories.repository.ArchivoRepository;
import com.toni.safememories.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArchivoService {

    private final ArchivoRepository archivoRepository;
    private final UsuarioRepository usuarioRepository;

    public ArchivoService(ArchivoRepository archivoRepository, UsuarioRepository usuarioRepository) {
        this.archivoRepository = archivoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Archivo crearArchivo(ArchivoRequest request, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)//saco el email del usuario autenticado (seguridad)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Archivo archivo = Archivo.builder()//construyo el objeto Archivo
                .nombreOriginal(request.getNombreOriginal())
                .nombreGuardado(request.getNombreGuardado())
                .ruta(request.getRuta())
                .tipo(request.getTipo())
                .tamano(request.getTamano())
                .usuario(usuario)
                .build();

        return archivoRepository.save(archivo);//guardo el archivo
    }
    public List<Archivo> obtenerArchivosDeUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return archivoRepository.findByUsuario(usuario);//devuelve sus archivos
    }
}
