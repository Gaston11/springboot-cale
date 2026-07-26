package com.cale.demo.controllers;

import com.cale.demo.dtos.ComentarioRequestDto;
import com.cale.demo.dtos.ComentarioResponseDto;
import com.cale.demo.dtos.PostRequestDto;
import com.cale.demo.dtos.PostResponseDto;
import com.cale.demo.exepciones.NoAutorizadoException;
import com.cale.demo.exepciones.OperacionInvalidaException;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.security.JwtAuthenticationFilter;
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

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void obtenerPostRetorna200() throws Exception {
        PostResponseDto  postResponseDto = new PostResponseDto();
        postResponseDto.setId(1L);
        postResponseDto.setTitulo("Post 1");

        when(postService.obtenerPostPorID(1L)).thenReturn(postResponseDto);

        mockMvc.perform(get("/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Post 1"));
    }

    @Test
    void obtenerPostQueNoExisteRetorna404() throws Exception {

        when(postService.obtenerPostPorID(any())).thenThrow(new RecursoNoEncontradoException("Post no encontrado"));

        mockMvc.perform(get("/post/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearPostRetorna201() throws Exception {
        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Post 1");
        postRequestDto.setDescripcion("Post 1");
        postRequestDto.setCategoriaIds(Set.of(10L));

        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(1L);
        postResponseDto.setTitulo("Post 1");
        postResponseDto.setDescripcion("Post 1");
        postResponseDto.setNombreCategorias(Set.of("Java"));

        when(postService.guardarPost(any(PostRequestDto.class))).thenReturn(postResponseDto);

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Post 1"))
                .andExpect(jsonPath("$.descripcion").value("Post 1"))
                .andExpect(jsonPath("$.nombreCategorias[0]").value("Java"));
    }

    @Test
    void guardarPostSinTituloRetorna400() throws Exception {
        String postDto = """
                {
                    "description": "Post 1"
                }
                """;

        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postDto))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminarPostValidoRetorna204() throws Exception {
        doNothing().when(postService).eliminarPost(1L);

        mockMvc.perform(delete("/post/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarPostNoValidoRetorna404() throws Exception {
        doThrow(new RecursoNoEncontradoException("Post no encontrado"))
                .when(postService)
                .eliminarPost(1L);

        mockMvc.perform(delete("/post/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void modificarPostRetorna200() throws Exception {
        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Post 1");
        postRequestDto.setDescripcion("Post 1");
        postRequestDto.setCategoriaIds(Set.of(10L));

        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(10L);
        postResponseDto.setTitulo("Post 1");
        postResponseDto.setDescripcion("Post 1");
        postResponseDto.setNombreCategorias(Set.of("Java"));

        when(postService.actualizarPost(any(PostRequestDto.class),eq(10L))).thenReturn(postResponseDto);

        mockMvc.perform(put("/post/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.titulo").value("Post 1"))
                .andExpect(jsonPath("$.descripcion").value("Post 1"))
                .andExpect(jsonPath("$.nombreCategorias[0]").value("Java"));
    }

    @Test
    void modificarPostQueNoExisteRetorna404() throws Exception {
        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Post 1");
        postRequestDto.setDescripcion("Post 1");
        postRequestDto.setCategoriaIds(Set.of(10L));

        when(postService.actualizarPost(any(PostRequestDto.class),eq(10L))).thenThrow(new RecursoNoEncontradoException("Post no encontrado"));

        mockMvc.perform(put("/post/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequestDto)))
            .andExpect(status().isNotFound());

    }

    @Test
    void modificarPostQueNoPerteneceAlUsuarioRetorna403() throws Exception {
        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Post 1");
        postRequestDto.setDescripcion("Post 1");
        postRequestDto.setCategoriaIds(Set.of(10L));

        when(postService.actualizarPost(any(PostRequestDto.class),eq(10L))).thenThrow(new NoAutorizadoException("Usuario no puede modificar el post"));

        mockMvc.perform(put("/post/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                .andExpect(status().isForbidden());

    }

    @Test
    void crearComentarioDevuelve201() throws Exception {
        ComentarioRequestDto comentarioRequestDto = new ComentarioRequestDto();
        comentarioRequestDto.setComentario("Comentario nuevo");

        ComentarioResponseDto comentarioResponseDto = new ComentarioResponseDto();
        comentarioResponseDto.setComentario("Comentario nuevo");
        comentarioResponseDto.setId(1L);

        when(postService.guardarComentario(eq(1L),any(ComentarioRequestDto.class))).thenReturn(comentarioResponseDto);

        mockMvc.perform(post("/post/1/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comentarioRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.comentario").value("Comentario nuevo"));

        verify(postService).guardarComentario(eq(1L),any(ComentarioRequestDto.class));

    }

    @Test
    void crearComentarioDevuelve404SiPostNoExiste() throws Exception {
        ComentarioRequestDto comentarioRequestDto = new ComentarioRequestDto();
        comentarioRequestDto.setComentario("Comentario nuevo");

        when(postService.guardarComentario(eq(1L),any(ComentarioRequestDto.class))).thenThrow(new RecursoNoEncontradoException("Post no encontrado"));

        mockMvc.perform(post("/post/1/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comentarioRequestDto)))
                .andExpect(status().isNotFound());

        verify(postService).guardarComentario(eq(1L),any(ComentarioRequestDto.class));
    }

    @Test
    void crearComentarioDevuelve400SiElDtoEsIvalido() throws Exception {
        ComentarioRequestDto comentarioRequestDto = new ComentarioRequestDto();
        comentarioRequestDto.setComentario("");

        when(postService.guardarComentario(eq(1L),any(ComentarioRequestDto.class))).thenThrow(OperacionInvalidaException.class);

        mockMvc.perform(post("/post/1/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comentarioRequestDto)))
                .andExpect(status().isBadRequest());

        verify(postService,never()).guardarComentario(eq(1L),any(ComentarioRequestDto.class));

    }

    @Test
    void obtenerComentarioDevuelve200() throws Exception {
        ComentarioResponseDto comentarioResponseDto = new ComentarioResponseDto();
        comentarioResponseDto.setComentario("Comentario nuevo");
        comentarioResponseDto.setId(1L);

        List<ComentarioResponseDto> comentarioResponseDtos = new LinkedList<>();
        comentarioResponseDtos.add(comentarioResponseDto);

        when(postService.obtenerComentarios(eq(1L))).thenReturn(comentarioResponseDtos);

        mockMvc.perform(get("/post/1/comentarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].comentario").value("Comentario nuevo"));

        verify(postService).obtenerComentarios(eq(1L));

    }

    @Test
    void obtenerComentarioDevuelve400SiPostNoExiste() throws Exception {

        when(postService.obtenerComentarios(eq(1L))).thenThrow(RecursoNoEncontradoException.class);

        mockMvc.perform(get("/post/1/comentarios"))
                .andExpect(status().isNotFound());

        verify(postService).obtenerComentarios(eq(1L));
    }
}
