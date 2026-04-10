package com.toni.safememories.config;

import com.toni.safememories.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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

    public SecurityConfig(JwtFilter jwtFilter) {//para usar mi filtro JWT dentro de la configuración
        this.jwtFilter = jwtFilter;
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
                .csrf(csrf -> csrf.disable()) //desactiva una protección que Spring usa mucho con formularios web
                .authorizeHttpRequests(auth -> auth
                        //cualquiera puede entrar a Post/usuarios/registro y /login
                        .requestMatchers(HttpMethod.POST, "/usuarios/registro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios/login").permitAll()
                        .anyRequest().authenticated()//el resto necesita autenticación
                )
                .httpBasic(Customizer.withDefaults())//no se va a usar, es el
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);//antes del filtro normal de usuario/contraseña,
                // pasa por mi filtro JWT

        return http.build();//termina la configuración y construye la seguridad final
    }
}