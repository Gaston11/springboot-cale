package com.cale.demo.services;

import com.cale.demo.dtos.ComentarioRequestDto;
import com.cale.demo.dtos.ComentarioResponseDto;
import com.cale.demo.exepciones.NoAutorizadoException;
import com.cale.demo.exepciones.OperacionInvalidaException;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.ComentarioModel;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.ComentarioRepository;
import com.cale.demo.repositories.UsuarioRepository;
import com.cale.demo.security.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ComentarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private ComentarioRepository comentarioRepository;
    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ComentarioService comentarioService;

    @Test
    void usuarioPuedeEditarComentarioPropio() {
        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(2L);

        ComentarioModel comentarioModel = new ComentarioModel();
        comentarioModel.setComentario("Comentario");
        comentarioModel.setId(1L);
        comentarioModel.setUsuario(usuarioModel);

        ComentarioRequestDto comentarioRequestDto = new ComentarioRequestDto();
        comentarioRequestDto.setComentario("Comentario editado");

        when(currentUserService.getCurrentUser()).thenReturn(usuarioModel);
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioModel));

        Assertions.assertDoesNotThrow(()-> comentarioService.editarComentario(1L,comentarioRequestDto));

        ArgumentCaptor<ComentarioModel> captor =
                ArgumentCaptor.forClass(ComentarioModel.class);

        verify(comentarioRepository).save(captor.capture());
        ComentarioModel comentarioModelGuardado = captor.getValue();

        assertEquals("Los Ids no son iguales",1L,comentarioModelGuardado.getId());
        assertEquals("Los comentarios no son iguales", "Comentario editado", comentarioModelGuardado.getComentario());
    }

    @Test
    void usuarioNoPuedeEditarComentarioAjeno(){
        UsuarioModel usuarioAjeno = new UsuarioModel();
        usuarioAjeno.setId(8L);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(2L);

        ComentarioModel comentarioModel = new ComentarioModel();
        comentarioModel.setComentario("Comentario");
        comentarioModel.setId(1L);
        comentarioModel.setUsuario(usuarioModel);

        ComentarioRequestDto comentarioRequestDto = new ComentarioRequestDto();
        comentarioRequestDto.setComentario("Comentario editado");

        when(currentUserService.getCurrentUser()).thenReturn(usuarioAjeno);
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioModel));

        Assertions.assertThrows(NoAutorizadoException.class, ()-> comentarioService.editarComentario(1L,comentarioRequestDto));
    }

    @Test
    void adminPuedeEditarComentarioAjeno(){
        UsuarioModel usuarioAdmin = new UsuarioModel();
        usuarioAdmin.setId(8L);
        usuarioAdmin.setRol(Rol.ADMIN);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(2L);

        ComentarioModel comentarioModel = new ComentarioModel();
        comentarioModel.setComentario("Comentario");
        comentarioModel.setId(1L);
        comentarioModel.setUsuario(usuarioModel);

        ComentarioRequestDto comentarioRequestDto = new ComentarioRequestDto();
        comentarioRequestDto.setComentario("Comentario editado");

        when(currentUserService.getCurrentUser()).thenReturn(usuarioAdmin);
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioModel));

        Assertions.assertDoesNotThrow(()-> comentarioService.editarComentario(1L,comentarioRequestDto));

        ArgumentCaptor<ComentarioModel> captor =
                ArgumentCaptor.forClass(ComentarioModel.class);

        verify(comentarioRepository).save(captor.capture());
        ComentarioModel comentarioModelGuardado = captor.getValue();

        assertEquals("Los Ids no son iguales",1L,comentarioModelGuardado.getId());
        assertEquals("Los comentarios no son iguales", "Comentario editado", comentarioModelGuardado.getComentario());
    }
}
