package com.cale.demo.security;

import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.UsuarioRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if  (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // es para que siga el flujo
            return;
        }

        try {
            String token = authHeader.substring(7);
            String email = jwtService.extraerEmail(token);
            UsuarioModel usuarioModel = usuarioRepository.findByEmail(email)
                    .orElseThrow(() ->
                    new UsernameNotFoundException("Usuario no encontrado"));
            List<GrantedAuthority> grantedAuthorities = List.of( new SimpleGrantedAuthority(
                    "ROLE_" + usuarioModel.getRol().name()));

            // SecurityContextHolder: “todavía NO hay usuario autenticado en este request”
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        grantedAuthorities); // “este usuario ya está autenticado”
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // agrego ip, sesion, detalles http
                SecurityContextHolder.getContext().setAuthentication(authToken); // “este request pertenece a este usuario"
            }
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
            {
                "mensaje": "Token expirado"
            }
            """);
        }
        catch (JwtException e) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            response.getWriter().write("""
            {
                "mensaje": "Token inválido"
            }
            """);

        }
    }
}
