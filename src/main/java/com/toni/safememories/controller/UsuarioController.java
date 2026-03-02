package com.toni.safememories.controller;

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

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService=usuarioService;

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

}
