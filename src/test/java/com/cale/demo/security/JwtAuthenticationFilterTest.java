package com.cale.demo.security;

import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.UsuarioRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter =
                new JwtAuthenticationFilter(jwtService, usuarioRepository);
    }

    @Test
    void noDebeSobrescribirAutenticacionExistente() throws Exception {

        UsuarioModel usuario = new UsuarioModel();
        usuario.setEmail("gaston@mail.com");
        usuario.setRol(Rol.USER);

        when(jwtService.extraerEmail("token-valido"))
                .thenReturn("gaston@mail.com");

        when(usuarioRepository.findByEmail("gaston@mail.com"))
                .thenReturn(Optional.of(usuario));

        UsernamePasswordAuthenticationToken autenticacionExistente =
                new UsernamePasswordAuthenticationToken(
                        "otra-identidad",
                        null
                );

        SecurityContextHolder.getContext()
                .setAuthentication(autenticacionExistente);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-valido");

        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertSame(
                autenticacionExistente,
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(filterChain).doFilter(request, response);

        SecurityContextHolder.clearContext();
    }
}