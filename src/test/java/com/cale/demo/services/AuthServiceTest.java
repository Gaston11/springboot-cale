package com.cale.demo.services;

import com.cale.demo.dtos.LoginRequest;
import com.cale.demo.dtos.RegisterRequest;
import com.cale.demo.exepciones.CredencialesInvalidasException;
import com.cale.demo.exepciones.OperacionInvalidaException;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.UsuarioRepository;
import com.cale.demo.security.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerDebeCrearUsuario() {

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNombre("nombre");
        registerRequest.setApellido("apellido");
        registerRequest.setEmail("test@mail.com");
        registerRequest.setPassword("password");

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setEmail("email");
        usuarioModel.setNombre("nombre");
        usuarioModel.setApellido("apellido");
        usuarioModel.setPrioridad(10);
        usuarioModel.setId(1L);

        when(passwordEncoder.encode("password")).thenReturn("password_encriptado");

        //Para que devuelva el mismo objeto que fue inyectado
        when(usuarioRepository.save(any(UsuarioModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Assertions.assertDoesNotThrow(() ->authService.register(registerRequest));

        ArgumentCaptor<UsuarioModel> captor =
                ArgumentCaptor.forClass(UsuarioModel.class);

        verify(usuarioRepository).save(captor.capture());
        UsuarioModel usuarioGuardado = captor.getValue();

        assertEquals("test@mail.com", usuarioGuardado.getEmail());
        assertEquals("password_encriptado", usuarioGuardado.getPassword());
        assertEquals(Rol.USER, usuarioGuardado.getRol());
    }

    @Test
    void registerDebeFallarSiEmailYaExiste(){
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNombre("nombre");
        registerRequest.setApellido("apellido");
        registerRequest.setEmail("test@mail.com");
        registerRequest.setPassword("password");

        UsuarioModel usuarioModelExistente = new UsuarioModel();
        usuarioModelExistente.setEmail("test@mail.com");

        when(usuarioRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(usuarioModelExistente));

        Assertions.assertThrows(OperacionInvalidaException.class,()->authService.register(registerRequest));

        verify(usuarioRepository,never()).save(any(UsuarioModel.class));
        verify(passwordEncoder,never()).encode(anyString());
    }

    @Test
    void loginDebeRetornarJwtCuandoCredencialesSonValidas(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("email@mail");
        loginRequest.setPassword("password");

        UsuarioModel  usuarioModel = new UsuarioModel();
        usuarioModel.setEmail("email@mail");
        usuarioModel.setPassword("passwordHash");

        when(usuarioRepository.findByEmail("email@mail")).thenReturn(Optional.of(usuarioModel));
        when(passwordEncoder.matches(anyString(),anyString())).thenReturn(true);
        when(jwtService.generarToken(loginRequest.getEmail())).thenReturn("token");

        String token = authService.login(loginRequest);
        assertEquals("token", token);

        verify(usuarioRepository).findByEmail("email@mail");
        verify(passwordEncoder).matches("password", "passwordHash");
        verify(jwtService).generarToken("email@mail");

    }

    @Test
    void loginDebeFallarSiUsuarioNoExiste(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("email@mail");
        loginRequest.setPassword("password");

        when(usuarioRepository.findByEmail("email@mail")).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class,
                () -> authService.login(loginRequest));

        verify(usuarioRepository,never()).save(any(UsuarioModel.class));
        verify(jwtService,never()).generarToken(anyString());
    }

    @Test
    void loginDebeFallarSiPasswordEsIncorrecta(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("email@mail");
        loginRequest.setPassword("password_incorrecta");

        UsuarioModel  usuarioModel = new UsuarioModel();
        usuarioModel.setEmail("email@mail");
        usuarioModel.setPassword("passwordHash");

        when(usuarioRepository.findByEmail("email@mail")).thenReturn(Optional.of(usuarioModel));
        when(passwordEncoder.matches("password_incorrecta","passwordHash")).thenReturn(false);

        Assertions.assertThrows(CredencialesInvalidasException.class, ()-> authService.login(loginRequest));

    }
}
