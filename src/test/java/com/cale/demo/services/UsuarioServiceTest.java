package com.cale.demo.services;

import com.cale.demo.dtos.PageResponse;
import com.cale.demo.dtos.PrioridadRequest;
import com.cale.demo.dtos.UsuarioResponseDto;
import com.cale.demo.exepciones.NoAutorizadoException;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.PostRepository;
import com.cale.demo.repositories.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void obtenerUsuarioPorId(){
        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setEmail("email");
        usuarioModel.setNombre("nombre");
        usuarioModel.setApellido("apellido");
        usuarioModel.setPrioridad(3);
        usuarioModel.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioModel));
        Assertions.assertDoesNotThrow(() -> usuarioService.obtenerPorId(1L));
        verify(usuarioRepository).findById(1L);

    }

    @Test
    void obtenerUsuarioPorIdDebeLanzarExepcionSiNoExiste(){

        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class,() -> usuarioService.obtenerPorId(999L));
        verify(usuarioRepository).findById(999L);

    }

    @Test
    void obtenerUsuariosNoDebeDevolverExepcion(){

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setEmail("email");
        usuarioModel.setNombre("nombre");
        usuarioModel.setApellido("apellido");
        usuarioModel.setPrioridad(3);
        usuarioModel.setId(1L);
        Pageable pageable = PageRequest.of(0, 1);
        Page<UsuarioModel> pagina =
                new PageImpl<>(List.of(usuarioModel), pageable, 1);

        when(usuarioRepository.findAll(pageable)).thenReturn(pagina);
        Assertions.assertDoesNotThrow(() -> usuarioService.obtenerUsuarios(pageable));
        verify(usuarioRepository).findAll(pageable);
    }

    @Test
    void obtenerUsuarioPorPrioridad(){

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setEmail("email");
        usuarioModel.setNombre("nombre");
        usuarioModel.setApellido("apellido");
        usuarioModel.setPrioridad(3);
        usuarioModel.setId(1L);

        when(usuarioRepository.findByPrioridad(3)).thenReturn(List.of(usuarioModel));
        Assertions.assertDoesNotThrow(() -> usuarioService.obtenerUsuariosPorPrioridad(3));
        verify(usuarioRepository).findByPrioridad(3);

    }

    @Test
    void obtenerUsuarioPorPrioridadDebeLanzarExepcionSiNoExiste(){

        when(usuarioRepository.findByPrioridad(99)).thenReturn(List.of());
        Assertions.assertThrows(RecursoNoEncontradoException.class,() -> usuarioService.obtenerUsuariosPorPrioridad(99));
        verify(usuarioRepository).findByPrioridad(99);

    }

    @Test
    void adminPuedeEliminarUsuario(){

        UsuarioModel  usuarioAdmin = new UsuarioModel();
        usuarioAdmin.setId(1L);
        usuarioAdmin.setRol(Rol.ADMIN);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setEmail("email");
        usuarioModel.setNombre("nombre");
        usuarioModel.setApellido("apellido");
        usuarioModel.setPrioridad(3);
        usuarioModel.setId(1L);

        when(currentUserService.getCurrentUser()).thenReturn(usuarioAdmin);
        when(usuarioRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        Assertions.assertDoesNotThrow(() -> usuarioService.eliminarUsuario(1L));
        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository).deleteById(1L);

    }

    @Test
    void usuarioNoPuedeEliminarUsuario(){

        UsuarioModel  usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setRol(Rol.USER);

        when(currentUserService.getCurrentUser()).thenReturn(usuario);
        assertThrows(NoAutorizadoException.class,() -> usuarioService.eliminarUsuario(1L));
        verify(usuarioRepository,never()).findById(1L);
        verify(usuarioRepository,never()).deleteById(1L);

    }

    @Test
    void eliminarUsuarioDebeLanzarExepcionSiNoExiste(){
        UsuarioModel  usuarioAdmin = new UsuarioModel();
        usuarioAdmin.setId(1L);
        usuarioAdmin.setRol(Rol.ADMIN);

        when(currentUserService.getCurrentUser()).thenReturn(usuarioAdmin);
        when(usuarioRepository.existsById(99L)).thenReturn(Boolean.FALSE);
        assertThrows(RecursoNoEncontradoException.class,() -> usuarioService.eliminarUsuario(99L));
        verify(usuarioRepository).existsById(99L);
        verify(usuarioRepository,never()).deleteById(99L);

    }

    @Test
    void adminDebeActualizarPrioridad(){
        UsuarioModel  usuarioAdmin = new UsuarioModel();
        usuarioAdmin.setId(1L);
        usuarioAdmin.setRol(Rol.ADMIN);

        UsuarioModel  usuario = new UsuarioModel();
        usuario.setId(2L);
        usuario.setRol(Rol.USER);
        usuario.setNombre("nombre");
        usuario.setApellido("apellido");
        usuario.setPrioridad(3);

        PrioridadRequest prioridadRequest = new PrioridadRequest();
        prioridadRequest.setPrioridad(1);

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioAdmin);
        Assertions.assertDoesNotThrow(()-> usuarioService.actualizarPrioridad(2L,prioridadRequest));
        verify(usuarioRepository).findById(2L);
        verify(usuarioRepository).save(usuario);

    }

    @Test
    void usuarioNoDebeActualizarPrioridad(){

        UsuarioModel  usuarioActual = new UsuarioModel();
        usuarioActual.setId(1L);
        usuarioActual.setRol(Rol.USER);

        UsuarioModel  usuario = new UsuarioModel();
        usuario.setId(2L);
        usuario.setRol(Rol.USER);
        usuario.setNombre("nombre");
        usuario.setApellido("apellido");
        usuario.setPrioridad(3);

        PrioridadRequest prioridadRequest = new PrioridadRequest();
        prioridadRequest.setPrioridad(1);

        when(currentUserService.getCurrentUser()).thenReturn(usuarioActual);
        assertThrows(NoAutorizadoException.class,()-> usuarioService.actualizarPrioridad(2L,prioridadRequest));
        verify(usuarioRepository,never()).findById(2L);
        verify(usuarioRepository,never()).save(usuario);
    }

    @Test
    void obtenerUsuariosDebeMapearCorrectamenteLasFechas() {

        LocalDateTime fechaCreacion = LocalDateTime.of(2026, 1, 10, 10, 0);
        LocalDateTime fechaModificacion = LocalDateTime.of(2026, 2, 15, 15, 30);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(1L);
        usuarioModel.setNombre("nombre");
        usuarioModel.setApellido("apellido");
        usuarioModel.setEmail("email@email.com");
        usuarioModel.setPrioridad(3);
        usuarioModel.setFechaCreacion(fechaCreacion);
        usuarioModel.setFechaModificacion(fechaModificacion);

        Pageable pageable = PageRequest.of(0, 1);
        Page<UsuarioModel> pagina =
                new PageImpl<>(List.of(usuarioModel), pageable, 1);

        when(usuarioRepository.findAll(pageable)).thenReturn(pagina);

        PageResponse<UsuarioResponseDto> resultado =
                usuarioService.obtenerUsuarios(pageable);

        UsuarioResponseDto dto = resultado.getContenido().get(0);

        Assertions.assertEquals(fechaCreacion, dto.getFechaCreacion());
        Assertions.assertEquals(fechaModificacion, dto.getFechaActualizacion());
    }
}
