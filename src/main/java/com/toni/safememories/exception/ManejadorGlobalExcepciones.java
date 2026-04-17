package com.toni.safememories.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> manejarArchivoDemasiadoGrande(MaxUploadSizeExceededException e) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)//devuelve 413 Payload Too Large
                .body(Map.of("error", "El archivo supera el tamaño máximo permitido de 10 MB"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> manejarErrorGeneral(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)//devuelve 500 Internal Server Error
                .body(Map.of("error", "Ha ocurrido un error interno"));
    }
}
