package com.toni.safememories.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//DTO es un objeto para que devuelva todos los datos excepto la contraseña

@AllArgsConstructor
@Getter
@NoArgsConstructor

public class UsuarioResponse {

    private long id;
    private String nombre;
    private String email;
}
