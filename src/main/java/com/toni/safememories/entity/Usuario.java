package com.toni.safememories.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

//Anotaciones de lombok
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

//Anotaciones JPA (Hibernate integrado a través de Spring Data JPA)
@Entity
@Table(name="usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String rol = "USER";
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    private boolean activo= true;

}
