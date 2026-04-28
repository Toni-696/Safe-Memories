package com.toni.safememories.repository;

import com.toni.safememories.entity.Carpeta;
import com.toni.safememories.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarpetaRepository extends JpaRepository<Carpeta, Long> {

    List<Carpeta> findByUsuario(Usuario usuario);

    boolean existsByNombreAndUsuario(String nombre, Usuario usuario);
}
