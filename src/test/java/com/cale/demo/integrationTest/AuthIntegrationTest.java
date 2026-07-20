package com.cale.demo.integrationTest;

import com.cale.demo.dtos.*;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.models.PostModel;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void crearPostYComprobarQueExista() throws Exception {
        registrarUsuario();
        String token = obtenerToken();

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Post 1");
        postRequestDto.setDescripcion("Descripcion 1");
        Set<Long> categorias = new HashSet<>();
        categorias.add(1L);
        postRequestDto.setCategoriaIds(Set.of(categoria.getId()));


        mockMvc.perform(post("/post")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                        .andExpect(status().isCreated());


        mockMvc.perform(get("/post")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido[0].titulo").value("Post 1"))
                .andExpect(jsonPath("$.contenido[0].descripcion").value("Descripcion 1"));

        assertEquals(1, postRepository.count());
        PostModel post = postRepository.findAll().getFirst();

        assertEquals("Post 1", post.getTitulo());
        assertEquals("Descripcion 1", post.getDescripcion());

    }

    @Test
    void agregarComentarioAPostYComprobarQueExista() throws Exception {
        registrarUsuario();
        String token = obtenerToken();

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Post 1");
        postRequestDto.setDescripcion("Descripcion 1");
        Set<Long> categorias = new HashSet<>();
        categorias.add(1L);
        postRequestDto.setCategoriaIds(Set.of(categoria.getId()));

        ComentarioRequestDto comentarioRequestDto = new ComentarioRequestDto();
        comentarioRequestDto.setComentario("Comentario 1");


        MvcResult postResult = mockMvc.perform(post("/post")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        PostResponseDto postCreado = objectMapper.readValue(
                postResult.getResponse().getContentAsString(),
                PostResponseDto.class);

        Long postId = postCreado.getId();

        mockMvc.perform(post("/post/{id}/comentarios",postId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comentarioRequestDto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/post/{id}/comentarios",postId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].comentario").value("Comentario 1"))
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void obtenerPostDevuelveLosPersistidos() throws Exception {
        registrarUsuario();

        UsuarioModel usuario = usuarioRepository
                .findByEmail("gaston@mail.com")
                .orElseThrow();

        PostModel post = new PostModel();
        post.setTitulo("Java");
        post.setDescripcion("Spring Boot");
        post.setUsuario(usuario);

        postRepository.save(post);

        mockMvc.perform(get("/post")
                        .header("Authorization", "Bearer " + obtenerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido[0].titulo").value("Java"));
    }

    void registrarUsuario() throws Exception {
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
    }

    String obtenerToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("gaston@mail.com");
        loginRequest.setPassword("123456");

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
