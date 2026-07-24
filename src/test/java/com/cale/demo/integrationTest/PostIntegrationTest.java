package com.cale.demo.integrationTest;

import com.cale.demo.dtos.*;
import com.cale.demo.models.*;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PostIntegrationTest {

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

    @Autowired
    private ComentarioRepository comentarioRepository;

    @BeforeEach
    void setUp() {
        CategoriaModel categoria = new CategoriaModel();
        categoria.setNombre("Java");

        this.categoria = categoriaRepository.save(categoria);
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

    @Test
    void crearPostYComprobarQueExista() throws Exception {
        registrarUsuario("gaston@mail.com");
        String token = obtenerToken("gaston@mail.com","123456");

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
    void eliminarPostLoBorraDeLaBase() throws Exception {

        registrarUsuario("gaston@mail.com");
        String token = obtenerToken("gaston@mail.com","123456");

        UsuarioModel usuario = usuarioRepository
                .findByEmail("gaston@mail.com")
                .orElseThrow();

        PostModel post = new PostModel();
        post.setTitulo("Java");
        post.setDescripcion("Spring Boot");
        post.setUsuario(usuario);

        postRepository.save(post);
        Long postId = postRepository.findAll().get(0).getId();

        mockMvc.perform(get("/post/{id}",postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("titulo").value("Java"))
                .andExpect(jsonPath("descripcion").value("Spring Boot"));


        mockMvc.perform(delete("/post/{id}",postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/post/{id}",postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        assertEquals(0, postRepository.count());

    }

    @Test
    void actualizarPostPersistiendoCambios()  throws Exception {
        registrarUsuario("gaston@mail.com");
        String token = obtenerToken("gaston@mail.com","123456");

        UsuarioModel usuario = usuarioRepository
                .findByEmail("gaston@mail.com")
                .orElseThrow();

        PostModel post = new PostModel();
        post.setTitulo("Java");
        post.setDescripcion("Spring Boot");
        post.setUsuario(usuario);

        postRepository.save(post);
        Long postId = postRepository.findAll().get(0).getId();

        mockMvc.perform(get("/post/{id}",postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("titulo").value("Java"))
                .andExpect(jsonPath("descripcion").value("Spring Boot"));


        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Java editado");
        postRequestDto.setDescripcion("Spring Boot editado");
        postRequestDto.setCategoriaIds(Set.of(categoria.getId()));

        mockMvc.perform(put("/post/{id}",postId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("titulo").value("Java editado"))
                .andExpect(jsonPath("descripcion").value("Spring Boot editado"));

        PostModel actualizado = postRepository.findById(postId).orElseThrow();
        assertEquals("Java editado", actualizado.getTitulo());
        assertEquals("Spring Boot editado", actualizado.getDescripcion());

    }

    @Test
    void obtenerPostDevuelveLosPersistidos() throws Exception {
        registrarUsuario("gaston@mail.com");

        UsuarioModel usuario = usuarioRepository
                .findByEmail("gaston@mail.com")
                .orElseThrow();

        PostModel post = new PostModel();
        post.setTitulo("Java");
        post.setDescripcion("Spring Boot");
        post.setUsuario(usuario);

        postRepository.save(post);

        mockMvc.perform(get("/post")
                        .header("Authorization", "Bearer " + obtenerToken("gaston@mail.com","123456")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido[0].titulo").value("Java"));
    }



    @Test
    void agregarComentarioAPostYComprobarQueExista() throws Exception {
        registrarUsuario("gaston@mail.com");
        String token = obtenerToken("gaston@mail.com","123456");

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Post 1");
        postRequestDto.setDescripcion("Descripcion 1");
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
    void eliminarComentario() throws Exception {
        registrarUsuario("gaston@mail.com");
        String token = obtenerToken("gaston@mail.com","123456");

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Post 1");
        postRequestDto.setDescripcion("Descripcion 1");
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

        MvcResult comentarioResult = mockMvc.perform(post("/post/{id}/comentarios",postId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comentarioRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        ComentarioResponseDto comentarioCreado = objectMapper.readValue(
                comentarioResult.getResponse().getContentAsString(),
                ComentarioResponseDto.class);

        mockMvc.perform(delete("/comentarios/{id}",comentarioCreado.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        Set<ComentarioModel> comentarioModelSet = comentarioRepository.findByPostId(postId);
        assertEquals(0,comentarioModelSet.size());

    }

    @Test
    void agregarVariosComentarioAPostYComprobarCantidadDeComentarios() throws Exception {
        registrarUsuario("gaston@mail.com");
        String token = obtenerToken("gaston@mail.com","123456");

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Post 1");
        postRequestDto.setDescripcion("Descripcion 1");
        postRequestDto.setCategoriaIds(Set.of(categoria.getId()));

        ComentarioRequestDto comentarioRequestDto = new ComentarioRequestDto();
        comentarioRequestDto.setComentario("Comentario 1");

        ComentarioRequestDto comentarioRequestDto2 = new ComentarioRequestDto();
        comentarioRequestDto2.setComentario("Comentario 2");


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

        mockMvc.perform(post("/post/{id}/comentarios",postId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comentarioRequestDto2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/post/{id}/comentarios",postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }



    @Test
    void usuarioNoPuedeEditarPostAjeno()  throws Exception {
        registrarUsuario("gaston@mail.com");

        UsuarioModel usuario = usuarioRepository
                .findByEmail("gaston@mail.com")
                .orElseThrow();

        PostModel post = new PostModel();
        post.setTitulo("Java");
        post.setDescripcion("Spring Boot");
        post.setUsuario(usuario);

        postRepository.save(post);
        Long postId = postRepository.findAll().get(0).getId();

        registrarUsuario("xxx@mail.com");
        String tokenNuevo = obtenerToken("xxx@mail.com","123456");

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Java editado");
        postRequestDto.setDescripcion("Spring Boot editado");
        postRequestDto.setCategoriaIds(Set.of(categoria.getId()));

        mockMvc.perform(put("/post/{id}",postId)
                        .header("Authorization", "Bearer " + tokenNuevo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                .andExpect(status().isForbidden());

    }

    @Test
    void adminPuedeEditarPostAjeno()  throws Exception {
        registrarUsuario("gaston@mail.com");
        UsuarioModel usuario = usuarioRepository
                .findByEmail("gaston@mail.com")
                .orElseThrow();

        PostModel post = new PostModel();
        post.setTitulo("Java");
        post.setDescripcion("Spring Boot");
        post.setUsuario(usuario);

        postRepository.save(post);
        Long postId = postRepository.findAll().get(0).getId();

        registrarUsuario("admin@mail.com");
        UsuarioModel admin = usuarioRepository
                .findByEmail("admin@mail.com")
                .orElseThrow();

        admin.setRol(Rol.ADMIN);
        usuarioRepository.save(admin);

        String tokenNuevo = obtenerToken("admin@mail.com","123456");

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Java editado por admin");
        postRequestDto.setDescripcion("Spring Boot editado por admin");
        postRequestDto.setCategoriaIds(Set.of(categoria.getId()));

        mockMvc.perform(put("/post/{id}",postId)
                        .header("Authorization", "Bearer " + tokenNuevo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Java editado por admin"))
                .andExpect(jsonPath("$.descripcion").value("Spring Boot editado por admin"));

        PostModel actualizado = postRepository.findById(postId).orElseThrow();

        assertEquals("Java editado por admin", actualizado.getTitulo());
        assertEquals("Spring Boot editado por admin", actualizado.getDescripcion());
    }

    @Test
    void adminPuedeEliminarPostAjeno()  throws Exception {
        registrarUsuario("gaston@mail.com");
        UsuarioModel usuario = usuarioRepository
                .findByEmail("gaston@mail.com")
                .orElseThrow();

        PostModel post = new PostModel();
        post.setTitulo("Java");
        post.setDescripcion("Spring Boot");
        post.setUsuario(usuario);

        postRepository.save(post);
        Long postId = postRepository.findAll().get(0).getId();

        registrarUsuario("admin@mail.com");
        UsuarioModel admin = usuarioRepository
                .findByEmail("admin@mail.com")
                .orElseThrow();

        admin.setRol(Rol.ADMIN);
        usuarioRepository.save(admin);

        String tokenNuevo = obtenerToken("admin@mail.com","123456");

        mockMvc.perform(delete("/post/{id}",postId)
                        .header("Authorization", "Bearer " + tokenNuevo))
                .andExpect(status().isNoContent());

        List<PostModel> list = postRepository.findAll();

        assertEquals(0,list.size());
    }

    @Test
    void crearUnPostConUsuarioAYUsuarioBIntentaEditar() throws Exception {
        registrarUsuario("usuarioA@mail.com");
        String token = obtenerToken("usuarioA@mail.com","123456");

        registrarUsuario("usuarioB@mail.com");

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Java");
        postRequestDto.setDescripcion("Spring Boot");
        postRequestDto.setCategoriaIds(Set.of(categoria.getId()));

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
        String tokenB = obtenerToken("usuarioB@mail.com","123456");

        PostRequestDto postRequestDto2 = new PostRequestDto();
        postRequestDto2.setTitulo("Java Editado");
        postRequestDto2.setDescripcion("Spring Boot Editado");
        postRequestDto2.setCategoriaIds(Set.of(categoria.getId()));

        mockMvc.perform(put("/post/{id}",postId)
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto2)))
                .andExpect(status().isForbidden());

    }

}
