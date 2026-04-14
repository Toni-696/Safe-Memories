package com.toni.safememories.controller;

import com.toni.safememories.dto.ArchivoRequest;
import com.toni.safememories.dto.ArchivoResponse;
import com.toni.safememories.entity.Archivo;
import com.toni.safememories.service.ArchivoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//EL CONTROLLER RECIBE PETICIONES HTTP, SACA INFORMACION (body, token), LLAMA AL SERVICE, Y DEVUELVE UNA RESPUESTA

@RestController
@RequestMapping("/archivos")
public class ArchivoController {

    private final ArchivoService archivoService;

    public ArchivoController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

    @PostMapping
    public ResponseEntity<?> crearArchivo(@RequestBody ArchivoRequest request, Authentication authentication) {
        try {
            String email = authentication.getName();//saco el email del usuario logueado, no del body

            Archivo archivo = archivoService.crearArchivo(request, email);//guarda el archivo y lo asigna al usuario

            ArchivoResponse response = new ArchivoResponse(//info que devolvemos al cliente, dto
                    archivo.getId(),
                    archivo.getNombreOriginal(),
                    archivo.getTipo(),
                    archivo.getTamano(),
                    archivo.getRuta(),
                    archivo.getUsuario().getEmail()
            );

            return ResponseEntity.ok(response);//devuelve 200 ok

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mis-archivos")
    //muestra los archivos del usuario loguado
    public ResponseEntity<?> obtenerMisArchivos(Authentication authentication) {
        try {
            String email = authentication.getName();//saco el nombre del token

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