package com.toni.safememories.controller;

import com.toni.safememories.dto.ArchivoResponse;
import com.toni.safememories.dto.CarpetaRequest;
import com.toni.safememories.dto.CarpetaResponse;
import com.toni.safememories.entity.Archivo;
import com.toni.safememories.entity.Carpeta;
import com.toni.safememories.service.CarpetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/carpetas")
public class CarpetaController {

    private final CarpetaService carpetaService;

    public CarpetaController(CarpetaService carpetaService) {
        this.carpetaService = carpetaService;
    }

    @PostMapping
    public ResponseEntity<?> crearCarpeta(@RequestBody CarpetaRequest request,
                                          Authentication authentication) {
        try {
            String email = authentication.getName();

            Carpeta carpeta = carpetaService.crearCarpeta(request, email);

            CarpetaResponse response = new CarpetaResponse(
                    carpeta.getId(),
                    carpeta.getNombre(),
                    carpeta.getFechaCreacion()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/mis-carpetas")
    public ResponseEntity<?> obtenerMisCarpetas(Authentication authentication) {
        try {
            String email = authentication.getName();

            List<Carpeta> carpetas = carpetaService.obtenerMisCarpetas(email);

            List<CarpetaResponse> response = carpetas.stream()
                    .map(carpeta -> new CarpetaResponse(
                            carpeta.getId(),
                            carpeta.getNombre(),
                            carpeta.getFechaCreacion()
                    ))
                    .toList();

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/archivos")
    public ResponseEntity<?> obtenerArchivosDeCarpeta(@PathVariable Long id,
                                                      Authentication authentication) {
        try {
            String email = authentication.getName();

            List<Archivo> archivos = carpetaService.obtenerArchivosDeCarpeta(id, email);

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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> renombrarCarpeta(@PathVariable Long id,
                                              @RequestBody CarpetaRequest request,
                                              Authentication authentication) {
        try {
            String email = authentication.getName();

            Carpeta carpeta = carpetaService.renombrarCarpeta(id, request, email);

            CarpetaResponse response = new CarpetaResponse(
                    carpeta.getId(),
                    carpeta.getNombre(),
                    carpeta.getFechaCreacion()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrarCarpeta(@PathVariable Long id,
                                           Authentication authentication) {
        try {
            String email = authentication.getName();

            carpetaService.borrarCarpeta(id, email);

            return ResponseEntity.ok(Map.of("mensaje", "Carpeta borrada correctamente"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/{id}/compartir")
    public ResponseEntity<?> compartirCarpeta(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {

        try {

            String emailPropietario = authentication.getName();

            carpetaService.compartirCarpeta(
                    id,
                    emailPropietario,
                    body.get("email")
            );

            return ResponseEntity.ok(
                    Map.of("mensaje", "Carpeta compartida correctamente")
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }
    @GetMapping("/compartidas")
    public ResponseEntity<?> obtenerCarpetasCompartidas(Authentication authentication) {
        try {
            String email = authentication.getName();

            List<Carpeta> carpetas = carpetaService.obtenerCarpetasCompartidasConmigo(email);

            List<CarpetaResponse> response = carpetas.stream()
                    .map(carpeta -> new CarpetaResponse(
                            carpeta.getId(),
                            carpeta.getNombre(),
                            carpeta.getFechaCreacion()
                    ))
                    .toList();

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/compartidas/{id}/archivos")
    public ResponseEntity<?> obtenerArchivosDeCarpetaCompartida(@PathVariable Long id,
                                                                Authentication authentication) {
        try {
            String email = authentication.getName();

            List<Archivo> archivos = carpetaService.obtenerArchivosDeCarpetaCompartida(id, email);

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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
