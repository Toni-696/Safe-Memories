package com.toni.safememories.dto;

import lombok.Data;

import java.util.List;

@Data
public class SolicitudDescargaRequest {

    private List<Long> archivosIds;
}