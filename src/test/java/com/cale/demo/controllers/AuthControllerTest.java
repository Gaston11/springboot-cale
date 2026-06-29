package com.cale.demo.controllers;

import com.cale.demo.dtos.RegisterRequest;
import com.cale.demo.dtos.UsuarioRequestDto;
import com.cale.demo.exepciones.OperacionInvalidaException;
import com.cale.demo.exepciones.RecursoYaExisteException;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.security.JwtAuthenticationFilter;
import com.cale.demo.services.AuthService;
import com.cale.demo.services.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.management.openmbean.OpenDataException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        registerRequest.setPrioridad(1);
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
        registerRequest.setPrioridad(1);
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
        registerRequest.setPrioridad(1);
        registerRequest.setEmail("prueba@test.com");
        registerRequest.setPassword("123456");

        when(authService.register(any(RegisterRequest.class))).thenThrow(RecursoYaExisteException.class);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());

        verify(authService).register(any(RegisterRequest.class));

    }
}
