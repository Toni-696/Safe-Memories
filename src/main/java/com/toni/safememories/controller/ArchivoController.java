package com.toni.safememories.controller;

import com.toni.safememories.dto.ArchivoRequest;
import com.toni.safememories.dto.ArchivoResponse;
import com.toni.safememories.entity.Archivo;
import com.toni.safememories.service.ArchivoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/archivos")
public class ArchivoController {

    private final ArchivoService archivoService;

    public ArchivoController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

    @PostMapping
    public ResponseEntity<?> crearArchivo(@RequestBody ArchivoRequest request) {
        try {
            Archivo archivo = archivoService.crearArchivo(request);

            ArchivoResponse response = new ArchivoResponse(
                    archivo.getId(),
                    archivo.getNombreOriginal(),
                    archivo.getTipo(),
                    archivo.getTamano(),
                    archivo.getRuta(),
                    archivo.getUsuario().getEmail()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mis-archivos")
    public ResponseEntity<?> obtenerMisArchivos(Authentication authentication) {
        try {
            String email = authentication.getName();

            List<Archivo> archivos = archivoService.obtenerArchivosDeUsuario(email);

            List<ArchivoResponse> response = archivos.stream()
                    .map(archivo -> new ArchivoResponse(
                            archivo.getId(),
                            archivo.getNombreOriginal(),
                            archivo.getTipo(),
                            archivo.getTamano(),
                            archivo.getRuta(),
                            archivo.getUsuario().getEmail()
                    ))
                    .toList();

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}