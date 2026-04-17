package com.toni.safememories.config;

import com.toni.safememories.security.JwtAuthenticationEntryPoint;
import com.toni.safememories.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


//ESTA CLASE ES EL PORTERO DE LA APP, decide quien entra sin problema, quien se tiene que identificar y
//y que método se usa para identificarse
/// usuarios/login y /usuarios/registro abierto, el resto con token

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(JwtFilter jwtFilter, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtFilter = jwtFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }


    @Bean // esta etiqueta le dice a Spring crea un objeto de este tipo y guárdalo para poder usarlo en otras clases
    public PasswordEncoder passwordEncoder() {// con BCrypt la contraseña se guarda en la DB cifrada
        return new BCryptPasswordEncoder(); // encripta contraseñas y las compara en login
    }

    @Bean //le pedimos a Spring el gestor principal de autenticación
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Configura qué rutas están abiertas y cuáles protegidas
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())//desactiva una seguridad de Spring que se suele usar en formularios
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                //exceptionHandling es el que va a manejar los fallos de autenticación
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/usuarios/registro", "/usuarios/login").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())//no se va a usar, es el
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);//antes del filtro normal de usuario/contraseña,
                // pasa por mi filtro JWT

        return http.build();//termina la configuración y construye la seguridad final
    }
}