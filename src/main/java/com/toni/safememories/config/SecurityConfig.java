package com.toni.safememories.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean // esta etiqueta le dice a Spring crea un objeto de este tipo y guárdalo para poder usarlo en otras clases
    public PasswordEncoder passwordEncoder() {// con BCrypt la contraseña se guarda en la DB cifrada
        return new BCryptPasswordEncoder(); // encripta contraseñas y las compara en login
    }

   /* @Bean //Solo para desarrollo, sino con POSTMAN no funciona por la seguridad
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // desactiva protección CSRF (para pruebas)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // permite todo sin autenticación
                );

        return http.build();
    }*/
}