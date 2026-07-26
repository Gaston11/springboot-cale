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
public class PostIntegrationTest extends IntegrationTestBase {

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



    @Test
    void crearPostYComprobarQueExista() throws Exception {
        registrarUsuario("gaston@mail.com");
        String token = obtenerToken("gaston@mail.com","123456");

        crearPost(token);

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

        String token = registrarYLoguear("gaston@mail.com");
        Long postId = crearPost(token);

        mockMvc.perform(get("/post/{id}",postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("titulo").value("Post 1"))
                .andExpect(jsonPath("descripcion").value("Descripcion 1"));


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
        String token = registrarYLoguear("gaston@mail.com");
        Long postId = crearPost(token);

        mockMvc.perform(get("/post/{id}",postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("titulo").value("Post 1"))
                .andExpect(jsonPath("descripcion").value("Descripcion 1"));


        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Java editado");
        postRequestDto.setDescripcion("Spring Boot editado");
        postRequestDto.setCategoriaIds(Set.of(this.crearCategoria("java",token)));

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
        String token = registrarYLoguear("gaston@mail.com");
        this.crearPost(token);

        mockMvc.perform(get("/post")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido[0].titulo").value("Post 1"));
    }

    @Test
    void agregarComentarioAPostYComprobarQueExista() throws Exception {
        String token = registrarYLoguear("gaston@mail.com");

        Long postId = crearPost(token);

        ComentarioRequestDto comentarioRequestDto = new ComentarioRequestDto();
        comentarioRequestDto.setComentario("Comentario 1");


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
        String token = this.registrarYLoguear("gaston@mail.com");
        Long postId = crearPost(token);

        ComentarioRequestDto comentarioRequestDto = new ComentarioRequestDto();
        comentarioRequestDto.setComentario("Comentario 1");


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
        String token = this.registrarYLoguear("gaston@mail.com");
        Long postId = crearPost(token);

        ComentarioRequestDto comentarioRequestDto = new ComentarioRequestDto();
        comentarioRequestDto.setComentario("Comentario 1");

        ComentarioRequestDto comentarioRequestDto2 = new ComentarioRequestDto();
        comentarioRequestDto2.setComentario("Comentario 2");


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
        String token = this.registrarYLoguear("gaston@mail.com");
        Long postId = crearPost(token);

        String tokenNuevo = this.registrarYLoguear("xxx@mail.com");

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Java editado");
        postRequestDto.setDescripcion("Spring Boot editado");
        Long idCategoria = this.crearCategoria("nuevo",tokenNuevo);
        postRequestDto.setCategoriaIds(Set.of(idCategoria));

        mockMvc.perform(put("/post/{id}",postId)
                        .header("Authorization", "Bearer " + tokenNuevo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                .andExpect(status().isForbidden());

    }

    @Test
    void adminPuedeEditarPostAjeno()  throws Exception {
        String token = registrarYLoguear("gaston@mail.com");
        Long postId = crearPost(token);

        crearUsuarioAdmin("admin@mail.com");
        String tokenNuevo = obtenerToken("admin@mail.com","123456");

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Java editado por admin");
        postRequestDto.setDescripcion("Spring Boot editado por admin");
        Long idCategoria = this.crearCategoria("nuevo",tokenNuevo);
        postRequestDto.setCategoriaIds(Set.of(idCategoria));

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
        String token = registrarYLoguear("gaston@mail.com");
        Long postId = crearPost(token);
        crearUsuarioAdmin("admin@mail.com");

        String tokenNuevo = obtenerToken("admin@mail.com","123456");

        mockMvc.perform(delete("/post/{id}",postId)
                        .header("Authorization", "Bearer " + tokenNuevo))
                .andExpect(status().isNoContent());

        List<PostModel> list = postRepository.findAll();

        assertEquals(0,list.size());
    }

    @Test
    void crearUnPostConUsuarioAYUsuarioBIntentaEditar() throws Exception {
        String token = registrarYLoguear("usuarioA@mail.com");
        String tokenB = registrarYLoguear("usuarioB@mail.com");

        Long postId = crearPost(token);

        PostRequestDto postRequestDto2 = new PostRequestDto();
        postRequestDto2.setTitulo("Java Editado");
        postRequestDto2.setDescripcion("Spring Boot Editado");
        Long idCategoria = this.crearCategoria("nuevo",tokenB);
        postRequestDto2.setCategoriaIds(Set.of(idCategoria));

        mockMvc.perform(put("/post/{id}",postId)
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto2)))
                .andExpect(status().isForbidden());

    }

    @Test
    void crearUnPostConTituloVacioDevuelve400() throws Exception {
        String token = registrarYLoguear("gaston@mail.com");

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setDescripcion("Spring Boot");
        Long idCategoria = this.crearCategoria("nuevo",token);
        postRequestDto.setCategoriaIds(Set.of(idCategoria));

        mockMvc.perform(post("/post")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearUnPostConCategoriasNulasDevuelve400() throws Exception {

        String token = registrarYLoguear("gaston@mail.com");

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Spring Boot");
        postRequestDto.setDescripcion("Spring Boot");

        mockMvc.perform(post("/post")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                .andExpect(status().isBadRequest());
    }


}
