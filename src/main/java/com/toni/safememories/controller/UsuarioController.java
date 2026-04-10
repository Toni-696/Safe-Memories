package com.toni.safememories.controller;

import com.toni.safememories.security.JwtService;
import com.toni.safememories.dto.LoginRequest;
import com.toni.safememories.dto.LoginResponse;
import com.toni.safememories.dto.UsuarioResponse;
import com.toni.safememories.entity.Usuario;
import com.toni.safememories.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
