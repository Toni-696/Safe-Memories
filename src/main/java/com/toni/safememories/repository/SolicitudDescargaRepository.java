package com.toni.safememories.repository;

import com.toni.safememories.entity.SolicitudDescarga;
import com.toni.safememories.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SolicitudDescargaRepository extends JpaRepository<SolicitudDescarga, Long> {

    @Query("SELECT DISTINCT s FROM SolicitudDescarga s LEFT JOIN FETCH s.archivos WHERE s.usuarioPropietario = :usuarioPropietario")
    List<SolicitudDescarga> findByUsuarioPropietarioConArchivos(@Param("usuarioPropietario") Usuario usuarioPropietario);

    @Query("SELECT s FROM SolicitudDescarga s LEFT JOIN FETCH s.archivos WHERE s.id = :id")
    Optional<SolicitudDescarga> findByIdConArchivos(@Param("id") Long id);

    @Query("SELECT DISTINCT s FROM SolicitudDescarga s LEFT JOIN FETCH s.archivos WHERE s.usuarioSolicitante = :usuarioSolicitante")
    List<SolicitudDescarga> findByUsuarioSolicitanteConArchivos(@Param("usuarioSolicitante") Usuario usuarioSolicitante);

    @Modifying
    @Query(value = "DELETE FROM solicitud_descarga_archivos WHERE archivo_id = :archivoId", nativeQuery = true)
    void deleteRelacionesArchivo(@Param("archivoId") Long archivoId);

    List<SolicitudDescarga> findByUsuarioSolicitante(Usuario usuarioSolicitante);
}