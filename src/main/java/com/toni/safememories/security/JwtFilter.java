package com.toni.safememories.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//ESTA CLASE MIRA SI HAY TOKEN, LO ABRE, COMPRUEBA VALIDEZ, IDENTIFICA AL USUARIO Y LE DICE A SPRING "este usuario ya esta autenticado"

@Component
public class JwtFilter extends OncePerRequestFilter { //extends Once... este filtro se ejecuta una vez por cada peticion http

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    public JwtFilter(JwtService jwtService, UsuarioDetailsService usuarioDetailsService) {
        this.jwtService = jwtService;//para leer el token
        this.usuarioDetailsService = usuarioDetailsService;//para buscar usuario en BD
    }

    @Override // este método se ejecuta en cada petición
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");//busca el header si no existe null

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; //aqui si no hay header "Authorization" o no empieza por bearer no hace nada, deja pasar la petición
        }

        String token = authHeader.substring(7);//quita "Bearer " 7 caracteres
        String email = jwtService.extraerEmail(token);

        // este filtro evalúa "Si ya hay un usuario autenticado, no lo repitas"
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            //busca usuario en la base de datos
            UserDetails userDetails = usuarioDetailsService.loadUserByUsername(email);

            //comprueba que el token es válido y que no ha sido manipulado
            if (jwtService.tokenValido(token, userDetails.getUsername())) {

                //CREA LA AUTENTICACION, le decimos a SPRING que este usuario está autenticado
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                //añade más info a la petición (IP, etc..)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //guarda en contexto de seguridad "este usuario ya está autenticado en esta petición"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // deja que la petición continúe hacia el controlador
        filterChain.doFilter(request, response);
    }
}
