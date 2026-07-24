package com.cale.demo.integrationTest;

import com.cale.demo.dtos.*;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.models.PostModel;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.CategoriaRepository;
import com.cale.demo.repositories.PostRepository;
import com.cale.demo.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private CategoriaModel categoria;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        CategoriaModel categoria = new CategoriaModel();
        categoria.setNombre("Java");

        this.categoria = categoriaRepository.save(categoria);
    }

    @Test
    void registerCreaUsuario() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setNombre("Gaston");
        request.setApellido("Perez");
        request.setEmail("gaston@mail.com");
        request.setPassword("123456");
        request.setPrioridad(1);

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
        request.setPrioridad(1);

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
        request.setPrioridad(1);

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
        //registrarUsuario("gaston@mail.com");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("gaston@mail.com");
        loginRequest.setPassword("123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

    }

    void registrarUsuario(String email) throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Gaston");
        request.setApellido("Perez");
        request.setEmail(email);
        request.setPassword("123456");
        request.setPrioridad(1);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    String obtenerToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String json = loginResult.getResponse().getContentAsString();
        LoginResponse response = objectMapper.readValue(json, LoginResponse.class);
        return response.getToken();
    }
}
