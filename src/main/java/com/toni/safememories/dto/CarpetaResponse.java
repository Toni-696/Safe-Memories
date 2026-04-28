package com.toni.safememories.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CarpetaResponse {

    private Long id;
    private String nombre;
    private LocalDateTime fechaCreacion;
}
