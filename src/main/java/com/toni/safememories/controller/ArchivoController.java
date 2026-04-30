package com.toni.safememories.controller;

import com.toni.safememories.dto.ArchivoRequest;
import com.toni.safememories.dto.ArchivoResponse;
import com.toni.safememories.dto.PermisoDescargaRequest;
import com.toni.safememories.entity.Archivo;
import com.toni.safememories.service.ArchivoService;
import com.toni.safememories.dto.ArchivoUpdateRequest;
import com.toni.safememories.dto.MoverArchivoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    @PostMapping("/subir")
    public ResponseEntity<?> subirArchivo(@RequestParam("archivo") MultipartFile archivo,
                                          @RequestParam(required = false) Long carpetaId,//puede venir un parámetro carpeta o no
                                          Authentication authentication) {
        try {
            String email = authentication.getName();

            Archivo nuevoArchivo = archivoService.subirArchivo(archivo, email, carpetaId);

            ArchivoResponse response = new ArchivoResponse(
                    nuevoArchivo.getId(),
                    nuevoArchivo.getNombreOriginal(),
                    nuevoArchivo.getTipo(),
                    nuevoArchivo.getTamano(),
                    nuevoArchivo.getRuta(),
                    nuevoArchivo.getUsuario().getEmail()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al subir el archivo");
        }
    }

    @GetMapping("/ver/{id}")
    public ResponseEntity<?> verArchivo(@PathVariable Long id, Authentication authentication) {
        try {
            String email = authentication.getName();

            Archivo archivo = archivoService.obtenerArchivoPorId(id, email);

            String nombreGuardado = archivo.getNombreGuardado();
            Path rutaArchivo = Paths.get(System.getProperty("user.dir"), "uploads", nombreGuardado);

            //Convierte el archivo físico en un recurso que Spring puede devolver en una respuesta HTTP
            Resource recurso = new UrlResource(rutaArchivo.toUri());

            if (!recurso.exists() || !recurso.isReadable()) {//comprueba que existe y se puede leer
                throw new RuntimeException("No se puede leer el archivo");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + archivo.getNombreOriginal() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, archivo.getTipo())
                    .body(recurso);

        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().body("Ruta del archivo no válida");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrarArchivo(@PathVariable Long id, Authentication authentication) {
        //@PathVariable coge el número de la url
        try {
            String email = authentication.getName();

            archivoService.borrarArchivo(id, email);

            return ResponseEntity.ok(Map.of("mensaje", "Archivo borrado correctamente"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/descargar/{id}")
    public ResponseEntity<?> descargarArchivo(@PathVariable Long id, Authentication authentication) {
        try {
            String email = authentication.getName();

            Archivo archivo = archivoService.obtenerArchivoParaDescarga(id, email);

            Path rutaArchivo = Paths.get(
                    System.getProperty("user.dir"),
                    "uploads",
                    archivo.getNombreGuardado()
            );

            Resource recurso = new UrlResource(rutaArchivo.toUri());
            //toURI convierte la ruta del archivo en una URI (forma standard de identificar un recurso)

            if (!recurso.exists() || !recurso.isReadable()) {
                throw new RuntimeException("No se puede leer el archivo");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo.getNombreOriginal() + "\"")
                    //attachment para descargar el archivo, y filename para guardarlo con el nombre que se le pasa
                    .header(HttpHeaders.CONTENT_TYPE, archivo.getTipo())
                    .body(recurso);//aqui se envía el archivo real

        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Ruta del archivo no válida"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/{id}/permisos-descarga")
    public ResponseEntity<?> concederPermisoDescarga(@PathVariable Long id,
                                                     @RequestBody PermisoDescargaRequest request,
                                                     Authentication authentication) {
        try {
            String emailPropietario = authentication.getName();

            archivoService.concederPermisoDescarga(
                    id,
                    emailPropietario,
                    request.getEmailUsuarioAutorizado()
            );

            return ResponseEntity.ok(Map.of("mensaje", "Permiso de descarga concedido correctamente"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/compartidos")
    public ResponseEntity<?> obtenerArchivosCompartidos(Authentication authentication) {
        try {
            String email = authentication.getName();

            List<Archivo> archivos = archivoService.obtenerArchivosCompartidosConmigo(email);

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
    @DeleteMapping("/{id}/permisos-descarga")
    public ResponseEntity<?> revocarPermisoDescarga(@PathVariable Long id,
                                                    @RequestBody PermisoDescargaRequest request,
                                                    Authentication authentication) {
        try {
            String emailPropietario = authentication.getName();

            archivoService.revocarPermisoDescarga(
                    id,
                    emailPropietario,
                    request.getEmailUsuarioAutorizado()
            );

            return ResponseEntity.ok(Map.of("mensaje", "Permiso de descarga revocado correctamente"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> renombrarArchivo(@PathVariable Long id,
                                              @RequestBody ArchivoUpdateRequest request,
                                              Authentication authentication) {
        try {
            String email = authentication.getName();

            Archivo archivo = archivoService.renombrarArchivo(
                    id,
                    request.getNombreOriginal(),
                    email
            );

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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/mover")
    public ResponseEntity<?> moverArchivo(@PathVariable Long id,
                                          @RequestBody MoverArchivoRequest request,
                                          Authentication authentication) {
        try {
            String email = authentication.getName();

            Archivo archivo = archivoService.moverArchivo(
                    id,
                    request.getCarpetaId(),
                    email
            );

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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}