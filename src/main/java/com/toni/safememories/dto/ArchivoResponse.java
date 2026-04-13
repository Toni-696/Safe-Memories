package com.toni.safememories.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArchivoResponse {// este el de respuesta

    private Long id;
    private String nombreOriginal;
    private String tipo;
    private Long tamano;
    private String ruta;
    private String emailUsuario;
}
