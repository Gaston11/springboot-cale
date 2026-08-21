package com.cale.demo.integrationTest;

import com.cale.demo.dtos.LoginRequest;
import com.cale.demo.dtos.PrioridadRequest;
import com.cale.demo.dtos.RegisterRequest;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.repositories.CategoriaRepository;
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

import static io.jsonwebtoken.Jwts.header;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UsuarioIntegrationTest extends IntegrationTestBase {

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

    @Test
    void buscarUsuarioPorPrioridad() throws Exception {
        String token = obtenerToken("demo@cale.com","demo1234");

        mockMvc.perform(get("/usuario/query?prioridad=10")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].prioridad").value(10));
    }

    @Test
    void usuarioConRolUserNoPuedeModificarPrioridad() throws Exception {
        String token = obtenerToken("demo@cale.com","demo1234");
        Long id = this.usuarioRepository.findByEmail("demo@cale.com").get().getId();
        PrioridadRequest prioridadRequest = new PrioridadRequest();
        prioridadRequest.setPrioridad(1);

        mockMvc.perform(patch("/usuario/{id}/prioridad",id)
                        .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(prioridadRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void usuarioConRolAdminPuedeModificarPrioridad() throws Exception {
        registrarUsuario("pruebaPrioridad@mail.com");
        crearUsuarioAdmin("admin@cale.com");
        String token = obtenerToken("admin@cale.com","123456");
        Long id = this.usuarioRepository.findByEmail("pruebaPrioridad@mail.com").get().getId();
        PrioridadRequest prioridadRequest = new PrioridadRequest();
        prioridadRequest.setPrioridad(1);

        mockMvc.perform(get("/usuario/{id}",id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prioridad").value(10));

        mockMvc.perform(patch("/usuario/{id}/prioridad",id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prioridadRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/usuario/{id}",id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prioridad").value(1));
    }

}
