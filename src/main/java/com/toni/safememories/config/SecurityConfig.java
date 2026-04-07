package com.toni.safememories.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
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

    // Configura qué rutas están abiertas y cuáles protegidas
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //desactiva una protección que Spring usa mucho con formularios web
                .authorizeHttpRequests(auth -> auth
                        //cualquiera puede entrar a Post/usuarios/registro y /login
                        .requestMatchers(HttpMethod.POST, "/usuarios/registro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios/login").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}