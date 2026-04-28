package com.toni.safememories.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "archivos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Archivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreOriginal;

    @Column(nullable = false)
    private String nombreGuardado;

    @Column(nullable = false)
    private String ruta;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Long tamano;

    private LocalDateTime fechaSubida = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "carpeta_id") // no es nullable=false para permitir subir archivos sin carpeta
    private Carpeta carpeta;
}