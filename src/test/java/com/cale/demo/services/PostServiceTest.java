package com.cale.demo.services;

import com.cale.demo.dtos.*;
import com.cale.demo.exepciones.NoAutorizadoException;
import com.cale.demo.exepciones.OperacionInvalidaException;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.*;
import com.cale.demo.repositories.CategoriaRepository;
import com.cale.demo.repositories.ComentarioRepository;
import com.cale.demo.repositories.PostRepository;
import com.cale.demo.repositories.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ComentarioRepository comentarioRepository;
    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private PostService postService;

    @Test
    void crearPostNoDebeLanzarExcepcion() {
        UsuarioModel  usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setRol(Rol.USER);

        CategoriaModel categoria = new CategoriaModel();
        categoria.setId(1L);
        categoria.setNombre("Categoria");

        UsuarioResponseDto usuarioResponseDto = new UsuarioResponseDto();
        usuarioResponseDto.setId(1L);

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Titulo");
        postRequestDto.setDescripcion("Descripcion");
        postRequestDto.setCategoriaIds(Set.of(1L));

        PostModel postNuevo = new PostModel();
        postNuevo.setId(10L);
        postNuevo.setUsuario(usuarioActual);
        postNuevo.setCategorias(Set.of(categoria));

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(postRepository.save(any(PostModel.class))).thenReturn(postNuevo);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);

        ArgumentCaptor<PostModel> captor =
                ArgumentCaptor.forClass(PostModel.class);

        Assertions.assertDoesNotThrow(() -> postService.guardarPost(postRequestDto));
        verify(postRepository).save(captor.capture());
        PostModel postModel = captor.getValue();

        Assertions.assertNotNull(postModel);
        Assertions.assertEquals( "Titulo",postModel.getTitulo());
        Assertions.assertEquals( "Descripcion",postModel.getDescripcion());
        Assertions.assertEquals(usuarioActual ,postModel.getUsuario());
        Assertions.assertEquals(Set.of(categoria),postModel.getCategorias());
    }

    @Test
    void debeLanzarExcepcionSiPostNoExiste(){

        when(postRepository.findById(999L)).
            thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> postService.obtenerPostPorID(999L));
    }

    @Test
    void noDebeLanzarExcepcionSiPostExisteAlBuscarlo(){
        PostModel post = new PostModel();
        post.setId(1L);
        post.setDescripcion("descripcion");
        post.setTitulo("titulo");
        post.setUsuario(new UsuarioModel());

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        Assertions.assertDoesNotThrow(() -> postService.obtenerPostPorID(1L));
        verify(postRepository).findById(1L);

        PostResponseDto resultado = postService.obtenerPostPorID(1L);
        Assertions.assertEquals(1L, resultado.getId());
        Assertions.assertEquals("titulo", resultado.getTitulo());
        Assertions.assertEquals("descripcion", resultado.getDescripcion());
    }

    @Test
    void usuarioNoDebeEditarPostAjeno(){
        UsuarioModel  usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setRol(Rol.USER);

        UsuarioModel usuarioNuevoConPost = new UsuarioModel();
        usuarioNuevoConPost.setId(2L);
        usuarioNuevoConPost.setRol(Rol.USER);

        PostModel postNuevo = new PostModel();
        postNuevo.setId(10L);
        postNuevo.setUsuario(usuarioNuevoConPost);

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Titulo");
        postRequestDto.setDescripcion("Descripcion");

        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(postRepository.findById(10L)).thenReturn(Optional.of(postNuevo));

        assertThrows(NoAutorizadoException.class,
                () -> postService.actualizarPost(postRequestDto,10L));
        verify(postRepository, never()).save(any());
    }

    @Test
    void adminPuedeEditarPostAjeno(){
        UsuarioModel  usuarioAdmin = new UsuarioModel();
        usuarioAdmin.setId(1L);
        usuarioAdmin.setRol(Rol.ADMIN);

        UsuarioModel usuarioNuevoConPost = new UsuarioModel();
        usuarioNuevoConPost.setId(2L);
        usuarioNuevoConPost.setRol(Rol.USER);

        CategoriaModel categoria = new CategoriaModel();
        categoria.setId(1L);
        categoria.setNombre("Categoria");

        PostModel postNuevo = new PostModel();
        postNuevo.setId(10L);
        postNuevo.setUsuario(usuarioNuevoConPost);
        postNuevo.setCategorias(Set.of(categoria));

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Titulo");
        postRequestDto.setDescripcion("Descripcion");

        postRequestDto.setCategoriaIds(Set.of(1L));

        when(currentUserService.getCurrentUser()).thenReturn(usuarioAdmin);
        when(postRepository.findById(10L)).thenReturn(Optional.of(postNuevo));
        when(postRepository.save(postNuevo)).thenReturn(postNuevo);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(new CategoriaModel()));

        Assertions.assertDoesNotThrow(() -> postService.actualizarPost(postRequestDto,10L));
        verify(postRepository).save(any(PostModel.class));
    }

    @Test
    void usuarioPuedeEditarPostPropio(){
        UsuarioModel  usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setRol(Rol.USER);

        CategoriaModel categoria = new CategoriaModel();
        categoria.setId(1L);
        categoria.setNombre("Categoria");

        PostModel postNuevo = new PostModel();
        postNuevo.setId(10L);
        postNuevo.setUsuario(usuarioActual);
        postNuevo.setCategorias(Set.of(categoria));

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Titulo");
        postRequestDto.setDescripcion("Descripcion");

        postRequestDto.setCategoriaIds(Set.of(1L));

        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(postRepository.findById(10L)).thenReturn(Optional.of(postNuevo));
        when(postRepository.save(postNuevo)).thenReturn(postNuevo);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(new CategoriaModel()));

        Assertions.assertDoesNotThrow(() -> postService.actualizarPost(postRequestDto,10L));
        verify(postRepository).save(any(PostModel.class));

    }

    @Test
    void guardarPostDebeLanzarExcepcionSiCategoriaNoExiste(){
        UsuarioModel  usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setRol(Rol.USER);

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Titulo");
        postRequestDto.setDescripcion("Descripcion");
        postRequestDto.setCategoriaIds(Set.of(99L));

        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        Assertions.assertThrows(RecursoNoEncontradoException.class,
                ()-> postService.guardarPost(postRequestDto));
    }

    @Test
    void actualizarPostDebeLanzarExcepcionSiCategoriaNoExiste(){
        UsuarioModel  usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setRol(Rol.USER);

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Titulo");
        postRequestDto.setDescripcion("Descripcion");
        postRequestDto.setCategoriaIds(Set.of(99L));

        CategoriaModel categoria = new CategoriaModel();
        categoria.setId(1L);
        categoria.setNombre("Categoria");

        PostModel postNuevo = new PostModel();
        postNuevo.setId(10L);
        postNuevo.setUsuario(usuarioActual);
        postNuevo.setTitulo("Titulo");
        postNuevo.setDescripcion("Descripcion");
        postNuevo.setCategorias(Set.of(categoria));

        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(postRepository.findById(10L)).thenReturn(Optional.of(postNuevo));
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RecursoNoEncontradoException.class,
                ()-> postService.actualizarPost(postRequestDto,10L));
    }

    @Test
    void actualizarPostDebeLanzarExcepcionSiNoTieneCategorias() {

        UsuarioModel usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setRol(Rol.USER);

        PostModel post = new PostModel();
        post.setId(10L);
        post.setUsuario(usuarioActual);

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Titulo");
        postRequestDto.setDescripcion("Descripcion");
        postRequestDto.setCategoriaIds(Set.of());

        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        Assertions.assertThrows(
                OperacionInvalidaException.class,
                () -> postService.actualizarPost(postRequestDto, 10L)
        );

        verify(postRepository, never()).save(any(PostModel.class));
    }

    @Test
    void eliminarPostDebeLanzarExcepcionSiPostNoExiste(){

        UsuarioModel  usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setRol(Rol.ADMIN);

        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(RecursoNoEncontradoException.class,
                ()-> postService.eliminarPost(1L));
    }

    @Test
    void usuarioNoPuedeEliminarPostAjeno(){
        UsuarioModel  usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setRol(Rol.USER);

        UsuarioModel usuarioPost = new UsuarioModel();
        usuarioPost.setId(2L);
        usuarioPost.setRol(Rol.USER);

        PostModel postNuevo = new PostModel();
        postNuevo.setId(10L);
        postNuevo.setUsuario(usuarioPost);
        postNuevo.setTitulo("Titulo");
        postNuevo.setDescripcion("Descripcion");

        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(postRepository.findById(10L)).thenReturn(Optional.of(postNuevo));

        Assertions.assertThrows(NoAutorizadoException.class,
                ()-> postService.eliminarPost(10L));
    }

    @Test
    void usuarioDebeEliminarPostCorrectamente(){

        UsuarioModel  usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setRol(Rol.USER);

        PostModel postNuevo = new PostModel();
        postNuevo.setId(10L);
        postNuevo.setUsuario(usuarioActual);
        postNuevo.setTitulo("Titulo");
        postNuevo.setDescripcion("Descripcion");

        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(postRepository.findById(10L)).thenReturn(Optional.of(postNuevo));

        Assertions.assertDoesNotThrow(()-> postService.eliminarPost(10L));
        verify(postRepository).deleteById(10L);

    }

    @Test
    void adminPuedeEliminarPostAjeno(){
        UsuarioModel  usuarioAdmin = new UsuarioModel();
        usuarioAdmin.setId(1L);
        usuarioAdmin.setRol(Rol.ADMIN);

        UsuarioModel usuarioNuevoConPost = new UsuarioModel();
        usuarioNuevoConPost.setId(2L);
        usuarioNuevoConPost.setRol(Rol.USER);

        CategoriaModel categoria = new CategoriaModel();
        categoria.setId(1L);
        categoria.setNombre("Categoria");

        PostModel postNuevo = new PostModel();
        postNuevo.setId(10L);
        postNuevo.setUsuario(usuarioNuevoConPost);
        postNuevo.setCategorias(Set.of(categoria));

        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Titulo");
        postRequestDto.setDescripcion("Descripcion");

        postRequestDto.setCategoriaIds(Set.of(1L));

        when(currentUserService.getCurrentUser()).thenReturn(usuarioAdmin);
        when(postRepository.findById(10L)).thenReturn(Optional.of(postNuevo));

        Assertions.assertDoesNotThrow(()-> postService.eliminarPost(10L));
        verify(postRepository).deleteById(10L);
    }

    @Test
    void obtenerPostsDebeFiltrarPorTitulo() {

        PostModel post = new PostModel();
        post.setId(1L);
        post.setDescripcion("descripcion");
        post.setTitulo("titulo");

        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNombre("Usuario");
        post.setUsuario(usuario);

        Pageable pageable = PageRequest.of(0, 1);

        Page<PostModel> pagina =
                new PageImpl<>(List.of(post), pageable, 1);

        when(postRepository.findByTituloContainingIgnoreCase(pageable, "titulo"))
                .thenReturn(pagina);

        Assertions.assertDoesNotThrow(
                () -> postService.obtenerPosts(pageable, "titulo", null)
        );

        verify(postRepository).findByTituloContainingIgnoreCase(pageable, "titulo");
        verify(postRepository, never()).findByUsuarioId(any(Pageable.class), anyLong());
    }

    @Test
    void obtenerPostsDebeFiltrarPorId() {

        PostModel post = new PostModel();
        post.setId(1L);
        post.setDescripcion("descripcion");
        post.setTitulo("titulo");

        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNombre("Usuario");
        post.setUsuario(usuario);

        Pageable pageable = PageRequest.of(0, 1);

        Page<PostModel> pagina =
                new PageImpl<>(List.of(post), pageable, 1);

        when(postRepository.findByUsuarioId(pageable, 1L))
                .thenReturn(pagina);

        Assertions.assertDoesNotThrow(
                () -> postService.obtenerPosts(pageable, null, 1L)
        );

        verify(postRepository).findByUsuarioId(pageable, 1L);
        verify(postRepository, never()).findByTituloContainingIgnoreCase(any(Pageable.class), anyString());
    }

    @Test
    void obtenerPostsDebeRetornarPageResponseCorrectamente() {

        PostModel post = new PostModel();
        post.setId(1L);
        post.setTitulo("titulo");
        post.setDescripcion("descripcion");

        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNombre("Usuario");
        post.setUsuario(usuario);

        Pageable pageable = PageRequest.of(0, 1);

        Page<PostModel> pagina =
                new PageImpl<>(List.of(post), pageable, 1);

        when(postRepository.findAll(pageable)).thenReturn(pagina);

        PageResponse<PostResponseDto> resultado =
                postService.obtenerPosts(pageable, null, null);

        Assertions.assertEquals(0, resultado.getPaginaActual());
        Assertions.assertEquals(1, resultado.getTamanioPagina());
        Assertions.assertEquals(1L, resultado.getTotalElementos());
        Assertions.assertEquals(1, resultado.getTotalPaginas());

        Assertions.assertEquals(1, resultado.getContenido().size());
        Assertions.assertEquals(1L, resultado.getContenido().get(0).getId());
        Assertions.assertEquals("titulo", resultado.getContenido().get(0).getTitulo());

        verify(postRepository).findAll(pageable);
    }

    @Test
    void guardarComentarioCreaComentarioCorrectamente() {

        UsuarioModel usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setNombre("Usuario");
        usuarioActual.setRol(Rol.USER);

        ComentarioRequestDto comentarioRequestDto = new ComentarioRequestDto();
        comentarioRequestDto.setComentario("Comentario 1");

        PostModel post = new PostModel();
        post.setId(1L);
        post.setTitulo("titulo");

        ComentarioModel comentarioGuardado = new ComentarioModel();
        comentarioGuardado.setId(1L);
        comentarioGuardado.setComentario("Comentario 1");
        comentarioGuardado.setUsuario(usuarioActual);
        comentarioGuardado.setPost(post);

        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(comentarioRepository.save(any(ComentarioModel.class)))
                .thenAnswer(invocation -> {
                    ComentarioModel comentario = invocation.getArgument(0);
                    comentario.setId(1L);
                    return comentario;
                });

        ComentarioResponseDto resultado =
                postService.guardarComentario(1L, comentarioRequestDto);

        ArgumentCaptor<ComentarioModel> captor =
                ArgumentCaptor.forClass(ComentarioModel.class);

        verify(comentarioRepository).save(captor.capture());

        ComentarioModel comentario = captor.getValue();

        Assertions.assertEquals("Comentario 1", comentario.getComentario());
        Assertions.assertEquals(usuarioActual, comentario.getUsuario());
        Assertions.assertEquals(post, comentario.getPost());

        Assertions.assertEquals(1L, resultado.getId());
        Assertions.assertEquals("Comentario 1", resultado.getComentario());
    }

    @Test
    void guardarComentarioEnUnPostQueNoExisteLanzaExcepcion() {

        UsuarioModel usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setNombre("Usuario");
        usuarioActual.setRol(Rol.USER);

        ComentarioRequestDto comentarioRequestDto = new ComentarioRequestDto();
        comentarioRequestDto.setComentario("Comentario 1");

        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(postRepository.findById(99L)).thenThrow(RecursoNoEncontradoException.class);

        Assertions.assertThrows(RecursoNoEncontradoException.class, () -> {
            postService.guardarComentario(99L, comentarioRequestDto);
        });
        verify(comentarioRepository, never()).save(any(ComentarioModel.class));

    }

    @Test
    void obtenerComentariosDevuelveLosComentariosCorrectamente() {

        UsuarioModel usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setNombre("Usuario");
        usuarioActual.setRol(Rol.USER);

        PostModel post = new PostModel();
        post.setId(1L);
        post.setTitulo("titulo");

        ComentarioModel comentarioGuardado = new ComentarioModel();
        comentarioGuardado.setId(1L);
        comentarioGuardado.setComentario("Comentario 1");
        comentarioGuardado.setUsuario(usuarioActual);
        comentarioGuardado.setPost(post);

        LocalDateTime fechaCreacion = LocalDateTime.of(2026, 6, 23, 15, 30);
        LocalDateTime fechaModificacion = LocalDateTime.of(2026, 6, 24, 10, 15);

        comentarioGuardado.setFechaCreacion(fechaCreacion);
        comentarioGuardado.setFechaModificacion(fechaModificacion);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(comentarioRepository.findByPostId(1L)).thenReturn(Set.of(comentarioGuardado));

        List<ComentarioResponseDto> resultado =
                postService.obtenerComentarios(1L);

        Assertions.assertEquals("Comentario 1", resultado.get(0).getComentario());
        Assertions.assertEquals(fechaCreacion, resultado.get(0).getFechaCreacion());
        Assertions.assertEquals(fechaModificacion, resultado.get(0).getFechaActualizacion());
    }

    @Test
    void obtenerComentariosLanzaExeptionSiNoExistePost() {

        UsuarioModel usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setNombre("Usuario");
        usuarioActual.setRol(Rol.USER);

        when(postRepository.findById(99L)).thenThrow(RecursoNoEncontradoException.class);

        Assertions.assertThrows(RecursoNoEncontradoException.class,
                ()-> postService.obtenerComentarios(99L));
    }

    @Test
    void obtenerPostsDebeDarPrioridadAlFiltroPorTitulo() {

        Pageable pageable = PageRequest.of(0, 1);

        PostModel post = new PostModel();
        post.setId(1L);
        post.setTitulo("titulo");

        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNombre("Usuario");
        post.setUsuario(usuario);

        Page<PostModel> pagina =
                new PageImpl<>(List.of(post), pageable, 1);

        when(postRepository.findByTituloContainingIgnoreCase(pageable, "titulo"))
                .thenReturn(pagina);

        PageResponse<PostResponseDto> resultado =
                postService.obtenerPosts(pageable, "titulo", 1L);

        Assertions.assertEquals(1, resultado.getContenido().size());

        verify(postRepository)
                .findByTituloContainingIgnoreCase(pageable, "titulo");

        verify(postRepository, never())
                .findByUsuarioId(any(Pageable.class), anyLong());

        verify(postRepository, never())
                .findAll(any(Pageable.class));
    }

}
