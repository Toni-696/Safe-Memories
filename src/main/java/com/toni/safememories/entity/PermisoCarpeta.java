package com.toni.safememories.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "permisos_carpetas")
public class PermisoCarpeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // carpeta compartida
    @ManyToOne
    @JoinColumn(name = "carpeta_id")
    private Carpeta carpeta;

    // usuario que puede verla
    @ManyToOne
    @JoinColumn(name = "usuario_autorizado_id")
    private Usuario usuarioAutorizado;
}
