package com.cale.demo.services;

import com.cale.demo.dtos.PostRequestDto;
import com.cale.demo.exepciones.NoAutorizadoException;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.models.PostModel;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.CategoriaRepository;
import com.cale.demo.repositories.PostRepository;
import com.cale.demo.repositories.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private PostService postService;

    @Test
    void DebeLanzarExcepcionSiPostNoExiste(){

        when(postRepository.findById(999L)).
            thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> postService.obtenerPostPorID(999L));
    }

    @Test
    void UsuarioNoDebeEditarPostAjeno(){
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

        postRequestDto.setCategoriasIds(Set.of(1L));

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

        postRequestDto.setCategoriasIds(Set.of(1L));

        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        when(postRepository.findById(10L)).thenReturn(Optional.of(postNuevo));
        when(postRepository.save(postNuevo)).thenReturn(postNuevo);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(new CategoriaModel()));

        Assertions.assertDoesNotThrow(() -> postService.actualizarPost(postRequestDto,10L));
        verify(postRepository).save(any(PostModel.class));

    }
}
