package com.cale.demo.services;

import com.cale.demo.dtos.UsuarioRequestDto;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void UsuarioCreadoNoDebeLanzarExcepcion() {

        UsuarioRequestDto usuarioRequestDto = new UsuarioRequestDto();
        usuarioRequestDto.setEmail("email");
        usuarioRequestDto.setNombre("nombre");
        usuarioRequestDto.setApellido("apellido");
        usuarioRequestDto.setPrioridad(10);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setEmail("email");
        usuarioModel.setNombre("nombre");
        usuarioModel.setApellido("apellido");
        usuarioModel.setPrioridad(10);
        usuarioModel.setId(1L);


        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioModel);

        Assertions.assertDoesNotThrow(() -> usuarioService.guardarUsuario(usuarioRequestDto));
        verify(usuarioRepository).save(any(UsuarioModel.class));

    }
}
