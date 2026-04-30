package com.toni.safememories.controller;

import com.toni.safememories.dto.*;
import com.toni.safememories.security.JwtService;
import com.toni.safememories.entity.Usuario;
import com.toni.safememories.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//esta es la capa que recibe peticiones HTTP
@RestController //Significa que devuelve JSON directamente
@RequestMapping("/usuarios") // todas las rutas empiezan por /usuarios
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public UsuarioController(UsuarioService usuarioService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    //Registro de usuario, postmapping es el endpoint /usuarios/registro
    @PostMapping("/registro")
    public ResponseEntity<?> registrar (@RequestBody Usuario usuario){ //@RequestBody convierte el JSON a OBJ usuario

        try{
            //usuarioService es quien valida, comprueba si existe email y guarda en base de datos
            Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);

            UsuarioResponse response = new UsuarioResponse(
                    nuevoUsuario.getId(),
                    nuevoUsuario.getNombre(),
                    nuevoUsuario.getEmail()
            );

            return ResponseEntity.ok(response);//"todo ha ido bien, devuelve 200 OK, y el usuario creado sin la contraseña"

        }catch (RuntimeException e){

            //si falla Service lanza la excepcion, HTTP 400 (badRequest)
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Usuario usuario = usuarioService.login( //busca al usuario en la db, comprueba contraseña y devuelve usuario
                    request.getEmail(),
                    request.getPassword()
            );
            //si todo va ok, genera el token y devuelve el usuario con el token como atributo
            String token = jwtService.generarToken(usuario.getEmail());

            LoginResponse response = new LoginResponse(
                    token,
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/perfil")
    public ResponseEntity<String> perfil() {
        System.out.println("He entrado en /usuarios/perfil");
        return ResponseEntity.ok("Acceso permitido con JWT");
    }
    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfil(@RequestBody UsuarioUpdateRequest request,
                                              Authentication authentication) {
        try {
            String email = authentication.getName();

            Usuario usuario = usuarioService.actualizarPerfil(email, request);

            UsuarioResponse response = new UsuarioResponse(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> cambiarPassword(@RequestBody CambiarPasswordRequest request,
                                             Authentication authentication) {
        try {
            String email = authentication.getName();

            usuarioService.cambiarPassword(email, request);

            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
