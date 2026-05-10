package com.toni.safememories.repository;

import com.toni.safememories.entity.Carpeta;
import com.toni.safememories.entity.PermisoCarpeta;
import com.toni.safememories.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermisoCarpetaRepository extends JpaRepository<PermisoCarpeta, Long> {

    boolean existsByCarpetaAndUsuarioAutorizado(
            Carpeta carpeta,
            Usuario usuarioAutorizado
    );


    List<PermisoCarpeta> findByUsuarioAutorizado(Usuario usuario);

}
