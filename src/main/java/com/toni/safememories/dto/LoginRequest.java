package com.toni.safememories.dto;

import lombok.Getter;
import lombok.Setter;


// este DTO es para la comparación de contraseñas en el login, solo necesito el email y la pass del usuario
// Spring convierte el JSON del usuario en este objeto con 2 atributos
@Setter
@Getter
public class LoginRequest {

    private String email;
    private String password;

}
