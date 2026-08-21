package com.cale.demo.controllers;

import com.cale.demo.dtos.LoginRequest;
import com.cale.demo.dtos.LoginResponse;
import com.cale.demo.dtos.RegisterRequest;
import com.cale.demo.exepciones.CredencialesInvalidasException;
import com.cale.demo.exepciones.RecursoYaExisteException;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.security.JwtAuthenticationFilter;
import com.cale.demo.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void registerRetorna200() throws Exception {

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNombre("Cale");
        registerRequest.setApellido("Cale");
        registerRequest.setEmail("prueba@test.com");
        registerRequest.setPassword("123456");

        UsuarioModel user = new UsuarioModel();
        user.setNombre("Cale");
        user.setApellido("Cale");
        user.setPrioridad(1);
        user.setRol(Rol.USER);
        user.setEmail("prueba@test.com");
        user.setId(1L);

        when(authService.register(any(RegisterRequest.class))).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Cale"))
                .andExpect(jsonPath("$.apellido").value("Cale"))
                .andExpect(jsonPath("$.email").value("prueba@test.com"));


    }

    @Test
    void registerRetorna400SiEmailEsInvalido() throws Exception {

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNombre("Cale");
        registerRequest.setApellido("Cale");
        registerRequest.setEmail("LALALA");
        registerRequest.setPassword("123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void registerRetorna400SiFaltanCampos() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNombre("Hola");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());

    }

    @Test
    void registerRetorna409SiMailYaExiste() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNombre("Cale");
        registerRequest.setApellido("Cale");
        registerRequest.setEmail("prueba@test.com");
        registerRequest.setPassword("123456");

        when(authService.register(any(RegisterRequest.class))).thenThrow(RecursoYaExisteException.class);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());

        verify(authService).register(any(RegisterRequest.class));

    }

    @Test
    void loginRetornaToken() throws Exception {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("algo@mail.com");
        loginRequest.setPassword("123456");

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken("jwt.token");

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token"));

        verify(authService).login(any(LoginRequest.class));

    }

    @Test
    void loginDevuelve401SiPasswordEsIncorrecta() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("algo@mail.com");
        loginRequest.setPassword("falso");

        when(authService.login(any(LoginRequest.class))).thenThrow(CredencialesInvalidasException.class);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

        verify(authService).login(any(LoginRequest.class));

    }

    @Test
    void loginDevuelve401SiMailNoExiste() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("falso@mail.com");
        loginRequest.setPassword("123456");

        when(authService.login(any(LoginRequest.class))).thenThrow(CredencialesInvalidasException.class);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

        verify(authService).login(any(LoginRequest.class));

    }
}
