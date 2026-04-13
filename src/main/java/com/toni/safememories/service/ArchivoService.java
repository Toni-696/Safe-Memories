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

    public Archivo crearArchivo(ArchivoRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmailUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        //primero busco el usuario al que pertenece el archivo

        Archivo archivo = Archivo.builder()//con builder creo el obj Archivo con todos sus datos
                .nombreOriginal(request.getNombreOriginal())
                .nombreGuardado(request.getNombreGuardado())
                .ruta(request.getRuta())
                .tipo(request.getTipo())
                .tamano(request.getTamano())
                .usuario(usuario)
                .build();

        return archivoRepository.save(archivo);// lo guardo
    }
    public List<Archivo> obtenerArchivosDeUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return archivoRepository.findByUsuario(usuario);//devuelve sus archivos
    }
}
