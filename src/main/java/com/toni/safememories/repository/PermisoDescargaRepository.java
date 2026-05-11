package com.toni.safememories.repository;

import com.toni.safememories.entity.Archivo;
import com.toni.safememories.entity.PermisoDescarga;
import com.toni.safememories.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermisoDescargaRepository extends JpaRepository<PermisoDescarga, Long> {

    boolean existsByArchivoAndUsuarioAutorizado(Archivo archivo, Usuario usuarioAutorizado);

    List<PermisoDescarga> findByUsuarioAutorizado(Usuario usuarioAutorizado);

    Optional<PermisoDescarga> findByArchivoAndUsuarioAutorizado(Archivo archivo, Usuario usuarioAutorizado);

    void deleteByArchivo(Archivo archivo);
}