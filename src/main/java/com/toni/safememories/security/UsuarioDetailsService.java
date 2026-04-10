package com.toni.safememories.security;

import com.toni.safememories.entity.Usuario;
import com.toni.safememories.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {// necesito el repository para buscar usuarios
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)//busca el usuario por email
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));//sino existe excepcion

        return new org.springframework.security.core.userdetails.User(//CONVIERTE OBJ USUARIO A USERDETAIL que entiende Spring
                usuario.getEmail(),
                usuario.getPassword(),//encriptada
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()))//define el rol o permiso
        );
    }
}
