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
    //Spring ve que UsuarioService necesita un PasswordEncoder.
    //Como ya existe un @bean de ese tipo en SecurityConfig, se lo inyecta automáticamente.
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
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));//.encode cifra la contraseña antes de guardar
        return usuarioRepository.save(usuario);
    }

    // Buscar por email
    public Optional<Usuario> buscarPorEmail(String email){
        return usuarioRepository.findByEmail(email); //Optional trata la posibilidad de que exista y de que no
    }

    public Usuario login(String email, String password) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) { // compara la contraseña
            // matches compara con el hash de la cifrada ambas, y devuelve true o false
            // password es la que escribe el usuario, usuario.getPassword es almacenada encriptada
            throw new RuntimeException("Contraseña incorrecta");
        }

        return usuario;
    }
}
