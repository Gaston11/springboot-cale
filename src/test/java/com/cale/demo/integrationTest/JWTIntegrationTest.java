package com.cale.demo.integrationTest;

import com.cale.demo.dtos.PostRequestDto;
import com.cale.demo.dtos.RegisterRequest;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.repositories.CategoriaRepository;
import com.cale.demo.repositories.ComentarioRepository;
import com.cale.demo.repositories.PostRepository;
import com.cale.demo.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class JWTIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void crearPostSinAutorizacionDevuelve403() throws Exception {

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Post 1");
        postRequestDto.setDescripcion("Descripcion 1");
        //postRequestDto.setCategoriaIds(Set.of(categoria.getId()));

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                .andExpect(status().isForbidden());

    }

    @Test
    void crearPostConTokenFalsoDevuelve401() throws Exception {

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Post 1");
        postRequestDto.setDescripcion("Descripcion 1");
        //postRequestDto.setCategoriaIds(Set.of(categoria.getId()));

        mockMvc.perform(post("/post")
                        .header("Authorization", "Bearer " + "tokenFalso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void tokenValidoPeroUsuarioNoExisteDevuelve401() throws Exception {
        registrarUsuario("gaston@mail.com");

        String token = obtenerToken("gaston@mail.com", "123456");
        Long usuarioId = obtenerIdPorEmail("gaston@mail.com");

        usuarioRepository.deleteById(usuarioId);

        mockMvc.perform(get("/post")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void autorizacionConEsquemaIncorrectoDevuelve403() throws Exception {
        mockMvc.perform(get("/post")
                        .header("Authorization", "Basic abc123"))
                .andExpect(status().isForbidden());
    }
}
