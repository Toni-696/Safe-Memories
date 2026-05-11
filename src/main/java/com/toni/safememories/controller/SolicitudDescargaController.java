package com.toni.safememories.controller;

import com.toni.safememories.dto.SolicitudDescargaRequest;
import com.toni.safememories.entity.Archivo;
import com.toni.safememories.entity.SolicitudDescarga;
import com.toni.safememories.service.SolicitudDescargaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/solicitudes-descarga")
public class SolicitudDescargaController {

    private final SolicitudDescargaService solicitudDescargaService;

    public SolicitudDescargaController(SolicitudDescargaService solicitudDescargaService) {
        this.solicitudDescargaService = solicitudDescargaService;
    }

    @PostMapping
    public ResponseEntity<?> crearSolicitud(@RequestBody SolicitudDescargaRequest request,
                                            Authentication authentication) {
        try {
            String emailSolicitante = authentication.getName();

            SolicitudDescarga solicitud = solicitudDescargaService.crearSolicitud(
                    emailSolicitante,
                    request
            );

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Solicitud de descarga creada correctamente",
                    "idSolicitud", solicitud.getId(),
                    "estado", solicitud.getEstado().name()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/recibidas")
    public ResponseEntity<?> obtenerSolicitudesRecibidas(Authentication authentication) {
        try {
            String emailPropietario = authentication.getName();

            List<SolicitudDescarga> solicitudes =
                    solicitudDescargaService.obtenerSolicitudesRecibidas(emailPropietario);

            List<Map<String, Object>> response = solicitudes.stream()
                    .map(solicitud -> Map.<String, Object>of(
                            "id", solicitud.getId(),
                            "estado", solicitud.getEstado().name(),
                            "fechaSolicitud", solicitud.getFechaSolicitud(),
                            "solicitante", solicitud.getUsuarioSolicitante().getEmail(),
                            "archivos", solicitud.getArchivos().stream()
                                    .map(Archivo::getNombreOriginal)
                                    .toList()
                    ))
                    .toList();

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/aceptar")
    public ResponseEntity<?> aceptarSolicitud(@PathVariable Long id,
                                              Authentication authentication) {
        try {
            String emailPropietario = authentication.getName();

            SolicitudDescarga solicitud =
                    solicitudDescargaService.aceptarSolicitud(id, emailPropietario);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Solicitud aceptada correctamente",
                    "estado", solicitud.getEstado().name()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(@PathVariable Long id,
                                               Authentication authentication) {
        try {
            String emailPropietario = authentication.getName();

            SolicitudDescarga solicitud =
                    solicitudDescargaService.rechazarSolicitud(id, emailPropietario);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Solicitud rechazada correctamente",
                    "estado", solicitud.getEstado().name()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/enviadas")
    public ResponseEntity<?> obtenerSolicitudesEnviadas(Authentication authentication) {
        try {
            String emailSolicitante = authentication.getName();

            List<SolicitudDescarga> solicitudes =
                    solicitudDescargaService.obtenerSolicitudesEnviadas(emailSolicitante);

            List<Map<String, Object>> response = solicitudes.stream()
                    .map(solicitud -> Map.<String, Object>of(
                            "id", solicitud.getId(),
                            "estado", solicitud.getEstado().name(),
                            "fechaSolicitud", solicitud.getFechaSolicitud(),
                            "propietario", solicitud.getUsuarioPropietario().getEmail(),
                            "archivos", solicitud.getArchivos().stream()
                                    .map(archivo -> Map.of(
                                            "id", archivo.getId(),
                                            "nombreOriginal", archivo.getNombreOriginal(),
                                            "tipo", archivo.getTipo()
                                    ))
                                    .toList()
                    ))
                    .toList();

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}