package com.cale.demo.services;

import com.cale.demo.exepciones.NoAutorizadoException;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.UsuarioRepository;
import com.cale.demo.security.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrentUserServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CurrentUserService currentUserService;

    @Test
    void retornaUsuarioAutenticado(){
        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(1L);
        usuarioModel.setEmail("email@email.com");
        usuarioModel.setNombre("nombre");
        usuarioModel.setRol(Rol.USER);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("email@email.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail("email@email.com")).thenReturn(Optional.of(usuarioModel));

        UsuarioModel usuarioAutenticado = currentUserService.getCurrentUser();

        Assertions.assertNotNull(usuarioAutenticado);
        Assertions.assertEquals(usuarioAutenticado.getEmail(), usuarioModel.getEmail());
        Assertions.assertEquals(usuarioAutenticado.getNombre(), usuarioModel.getNombre());
        Assertions.assertEquals(usuarioAutenticado.getRol(), usuarioModel.getRol());
    }

    @Test
    void lanzaExcepcionSiNoHayUsuarioAutenticado(){

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(null);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> currentUserService.getCurrentUser());
    }
}
