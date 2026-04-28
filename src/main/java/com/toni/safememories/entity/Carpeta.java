package com.toni.safememories.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "carpetas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carpeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist // esta etiqueta soluciona el problema que tenía con las fechas,
    // a veces no se aplica bien cuando JPA/Hibernate crea el objeto hace que asigne la fecha antes
    public void asignarFechaCreacion() {
        this.fechaCreacion = LocalDateTime.now();
    }
}