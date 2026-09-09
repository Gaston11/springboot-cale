package com.cale.demo.services;

import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.repositories.CategoriaRepository;
import com.cale.demo.repositories.UsuarioRepository;
import com.cale.demo.security.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

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
    public void eliminarCategoriaNoDebeLanzarExcepcionSiExiste() {

        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setId(1L);
        categoriaModel.setNombre("Categoria 1");

        when(categoriaRepository.findById(1L)).
                thenReturn(Optional.of(categoriaModel));

        Assertions.assertDoesNotThrow(() -> categoriaService.eliminarCategoria(1L));

        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    public void crearCategoriaDebeGuardarCategoria() {

        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setId(1L);
        categoriaModel.setNombre("Categoria 1");

        when(categoriaRepository.save(categoriaModel)).thenReturn(categoriaModel);

        CategoriaModel resultado =
                categoriaService.guardarCategoria(categoriaModel);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1L, resultado.getId());
        Assertions.assertEquals("Categoria 1", resultado.getNombre());

        //Assertions.assertDoesNotThrow(() -> categoriaService.guardarCategoria(categoriaModel));
        verify(categoriaRepository).save(categoriaModel);
    }

}
