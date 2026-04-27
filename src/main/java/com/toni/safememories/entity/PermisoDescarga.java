package com.toni.safememories.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "permisos_descarga")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermisoDescarga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "archivo_id", nullable = false)
    private Archivo archivo;

    @ManyToOne
    @JoinColumn(name = "usuario_autorizado_id", nullable = false)
    private Usuario usuarioAutorizado;
}
