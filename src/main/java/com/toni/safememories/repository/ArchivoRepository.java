package com.toni.safememories.repository;

import com.toni.safememories.entity.Archivo;
import com.toni.safememories.entity.Carpeta;
import com.toni.safememories.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {

    List<Archivo> findByUsuario(Usuario usuario);
    List<Archivo> findByCarpeta(Carpeta carpeta);

}
