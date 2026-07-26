package com.cale.demo.integrationTest;

import com.cale.demo.dtos.LoginRequest;
import com.cale.demo.dtos.RegisterRequest;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.repositories.CategoriaRepository;
import com.cale.demo.repositories.ComentarioRepository;
import com.cale.demo.repositories.PostRepository;
import com.cale.demo.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void loginConEmailInvalidoDevulve401() throws Exception {
        registrarUsuario("gaston@mail.com");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("mail@mail.com");
        loginRequest.setPassword("123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void loginConPasswordVaciaDevulve400() throws Exception {
        registrarUsuario("gaston@mail.com");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("mail@mail.com");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

    }
}
