package com.toni.safememories.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service // le dice a Spring que esta clase forma parte de la lógica de la aplicación
// asi luego puede aplicarse en el controller o en el service
public class JwtService {

    @Value("${jwt.secret}") // Spring mete en secret la clave de resources/application.properties
    private String secret;

    private SecretKey secretKey; //aqui se guarda la clave preparada para firmar el token

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        //Esto se ejecuta automáticamente cuando Spring crea el objeto.
        //1- coge el texto secreto  2-lo convierte en bytes   3-crea una clave válida para firmar JWT
    }

    public String generarToken(String email) {
        return Jwts.builder()
                .subject(email) //identifica al usuario por su email
                .issuedAt(new Date()) //fecha en la que se crea
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // caduca en 1h
                .signWith(secretKey) //se firma con la clave secreta
                .compact(); // lo convierte en el texto final del token
    }

    public String extraerEmail(String token) { //le pasamos el token y devuelve el email que se guardó dentro
        return Jwts.parser()//perpara la herramienta para leer el token
                .verifyWith(secretKey)//usa la clave secreta para comprobar que el token es auténtico
                .build()
                .parseSignedClaims(token)//abre el token y lee su contenido
                .getPayload()//dame la parte del token donde están los datos
                .getSubject(); //dame el email del token
    }

    public boolean tokenValido(String token, String email) { //comprueba que el token pertenece a ese usuario
        String emailToken = extraerEmail(token);
        return emailToken.equals(email);
    }
}