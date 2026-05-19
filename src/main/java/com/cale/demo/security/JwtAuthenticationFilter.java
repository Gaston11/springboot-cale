package com.cale.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        System.out.println("AUTH HEADER: " + authHeader);

        if  (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // es para que siga el flujo
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtService.extraerEmail(token);

        // SecurityContextHolder: “todavía NO hay usuario autenticado en este request”
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    new ArrayList<>()); // “este usuario ya está autenticado”
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // agrego ip, sesion, detalles http
            SecurityContextHolder.getContext().setAuthentication(authToken); // “este request pertenece a este usuario"
        }
        filterChain.doFilter(request, response);
    }
}
