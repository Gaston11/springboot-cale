package com.cale.demo.integrationTest;

import com.cale.demo.dtos.*;
import com.cale.demo.repositories.CategoriaRepository;
import com.cale.demo.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthIntegrationTest extends IntegrationTestBase{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;


    @Test
    void registerCreaUsuario() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setNombre("Gaston");
        request.setApellido("Perez");
        request.setEmail("gaston@mail.com");
        request.setPassword("123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertTrue(
                usuarioRepository.findByEmail("gaston@mail.com")
                        .isPresent()
        );
    }

    @Test
    void registerCreaUsuarioYLuegoPuedoLoguear() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setNombre("Gaston");
        request.setApellido("Perez");
        request.setEmail("gaston@mail.com");
        request.setPassword("123456");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("gaston@mail.com");
        loginRequest.setPassword("123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", startsWith("eyJ")));
    }


    @Test
    void noPermiteRegistrarEmailDuplicado() throws Exception {
        registrarUsuario("gaston@mail.com");

        RegisterRequest request = new RegisterRequest();
        request.setNombre("Gaston");
        request.setApellido("Perez");
        request.setEmail("gaston@mail.com");
        request.setPassword("123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        assertTrue(
                usuarioRepository.findByEmail("gaston@mail.com")
                        .isPresent()
        );
    }

    @Test
    void loginConPasswordIncorrectaDevuelve401() throws Exception {
        registrarUsuario("gaston@mail.com");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("gaston@mail.com");
        loginRequest.setPassword("Error");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void loginConEmailInexistenteDevuelve401() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("gaston@mail.com");
        loginRequest.setPassword("123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void registerConEmailInvalidoDevuelve400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Gaston");
        request.setApellido("Perez");
        request.setEmail("email-invalido");
        request.setPassword("123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertTrue(
                usuarioRepository.findByEmail("email-invalido").isEmpty()
        );
    }

}
