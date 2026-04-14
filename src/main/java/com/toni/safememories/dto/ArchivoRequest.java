package com.toni.safememories.dto;

import lombok.Data;

@Data
public class ArchivoRequest {//este dto reicbe los datos necesarios para crear un archivo

    private String nombreOriginal;
    private String nombreGuardado;
    private String ruta;
    private String tipo;
    private Long tamano;
    }
