package com.toni.safememories.repository;

import com.toni.safememories.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository <Usuario, Long> {
    //manejo Usuario con la clave primaria que es de tipo long
    //save(), findByID(), findAll(), deleteById(), count... (JpaRepository)

    Optional<Usuario> findByEmail(String email); //para login, Optional permite null

    boolean existsByEmail(String email);//para registro
}
