package com.toni.safememories.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

//Esta clase controla que cuando alguien intente entrar a una ruta protegida sin estar autenticado
//responda con este Json

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {//maneja los errores de autenticacion

    @Override //Spring llama a este método cuando la autenticacion falla
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {
                  "error": "No autorizado. Debes iniciar sesión o enviar un token válido."
                }
                """);
    }
}
