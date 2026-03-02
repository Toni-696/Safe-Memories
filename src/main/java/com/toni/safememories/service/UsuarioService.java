package com.toni.safememories.service;

import com.toni.safememories.entity.Usuario;
import com.toni.safememories.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;//cifrar la contraseña

    //Constructor
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Registrar un usuario
    public Usuario registrarUsuario(Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())){
            throw new RuntimeException("El email ya está registrado");
        }
        //si el email no está en uso guarda el usuario en la DB
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));//cifra la contraseña antes de guardar
        return usuarioRepository.save(usuario);
    }

    // Buscar por email
    public Optional<Usuario> buscarPorEmail(String email){
        return usuarioRepository.findByEmail(email);
    }
}
