package com.cale.demo.services;

import com.cale.demo.exepciones.NoAutorizadoException;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.CategoriaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    public void obtenerCategoriaPoIdNoDebeLanzarExcepcion() {

        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setId(1L);
        categoriaModel.setNombre("Categoria 1");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaModel));
        Assertions.assertDoesNotThrow(()-> categoriaService.obtenerCategoriaPorID(1L));
        verify(categoriaRepository).findById(1L);

    }

    @Test
    public void obtenerCategoriaDebeLanzarExcepcionSiNoExiste() {

        when(categoriaRepository.findById(999L)).
                thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> categoriaService.obtenerCategoriaPorID(999L));

    }

    @Test
    public void obtenerCategoriasNoDebeLanzarExcepcion() {

        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setId(1L);
        categoriaModel.setNombre("Categoria 1");

        when(categoriaRepository.findAll()).thenReturn(List.of(categoriaModel));
        Assertions.assertDoesNotThrow(()-> categoriaService.obtenerCategorias());
        verify(categoriaRepository).findAll();

    }

    @Test
    public void eliminarCategoriaDebeLanzarExcepcionSiNoExiste() {

        when(categoriaRepository.findById(999L)).
                thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> categoriaService.eliminarCategoria(999L));
        verify(categoriaRepository).findById(999L);
    }

    @Test
    public void eliminarCategoriaPorAdminNoDebeLanzarExcepcionSiExiste() {

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(1L);
        usuarioModel.setRol(Rol.ADMIN);

        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setId(1L);
        categoriaModel.setNombre("Categoria 1");

        when(categoriaRepository.findById(1L)).
                thenReturn(Optional.of(categoriaModel));
        when(currentUserService.getCurrentUser()).thenReturn(usuarioModel);

        Assertions.assertDoesNotThrow(() -> categoriaService.eliminarCategoria(1L));

        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    public void crearCategoriaPorAdminDebeGuardarCategoria() {

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(1L);
        usuarioModel.setRol(Rol.ADMIN);

        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setId(1L);
        categoriaModel.setNombre("Categoria 1");

        when(categoriaRepository.save(categoriaModel)).thenReturn(categoriaModel);
        when(currentUserService.getCurrentUser()).thenReturn(usuarioModel);

        CategoriaModel resultado =
                categoriaService.guardarCategoria(categoriaModel);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1L, resultado.getId());
        Assertions.assertEquals("Categoria 1", resultado.getNombre());

        verify(categoriaRepository).save(categoriaModel);
    }

    @Test
    public void eliminarCategoriaPorUserDebeLanzarExcepcion() {

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(1L);
        usuarioModel.setRol(Rol.USER);

        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setId(1L);
        categoriaModel.setNombre("Categoria 1");

        when(categoriaRepository.findById(1L)).
                thenReturn(Optional.of(categoriaModel));
        when(currentUserService.getCurrentUser()).thenReturn(usuarioModel);

        Assertions.assertThrows(NoAutorizadoException.class,
                () -> categoriaService.eliminarCategoria(1L));

        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository, never()).deleteById(1L);
    }

    @Test
    public void crearCategoriaPorUserDebeLanzarExcepcion() {

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(1L);
        usuarioModel.setRol(Rol.USER);

        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setId(1L);
        categoriaModel.setNombre("Categoria 1");

        when(currentUserService.getCurrentUser()).thenReturn(usuarioModel);
        Assertions.assertThrows(NoAutorizadoException.class,
                ()-> categoriaService.guardarCategoria(categoriaModel));

        verify(categoriaRepository, never()).save(categoriaModel);
    }



}
