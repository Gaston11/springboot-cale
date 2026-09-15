package com.cale.demo.controllers;

import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.security.JwtAuthenticationFilter;
import com.cale.demo.services.CategoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = CategoriaController.class)
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void obtenerCategoriaRetorna200() throws Exception{
        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setId(1L);
        categoriaModel.setNombre("Categoria 1");

        ArrayList<CategoriaModel> categoriaModels = new ArrayList<>();
        categoriaModels.add(categoriaModel);

        when(categoriaService.obtenerCategorias()).thenReturn(categoriaModels);

        mockMvc.perform(get("/categoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Categoria 1"));

        verify(categoriaService).obtenerCategorias();
    }

    @Test
    void guardarCategoriaRetorna201() throws Exception{
        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setId(1L);
        categoriaModel.setNombre("Categoria 1");

        when(categoriaService.guardarCategoria(any(CategoriaModel.class))).thenReturn(categoriaModel);

        mockMvc.perform(post("/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaModel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Categoria 1"));

        verify(categoriaService).guardarCategoria(any(CategoriaModel.class));
    }

    @Test
    void obtenerCategoriaRetorna404SiNoExiste() throws Exception{

        when(categoriaService.obtenerCategoriaPorID(99L) ).thenThrow(RecursoNoEncontradoException.class);

        mockMvc.perform(get("/categoria/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(categoriaService).obtenerCategoriaPorID(99L);
    }

    @Test
    void eliminarCategoriaRetorna204() throws Exception{

        doNothing().when(categoriaService).eliminarCategoria(1L);

        mockMvc.perform(delete("/categoria/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(categoriaService).eliminarCategoria(1L);
    }

    @Test
    void eliminarCategoriaRetorna404SiNoExiste() throws Exception{
        doThrow(new RecursoNoEncontradoException("Categoria no encontrada"))
                .when(categoriaService)
                .eliminarCategoria(99L);

        mockMvc.perform(delete("/categoria/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(categoriaService).eliminarCategoria(99L);

    }

    @Test
    void obtenerCategoriaPorIdRetorna200() throws Exception {
        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setId(1L);
        categoriaModel.setNombre("Categoria 1");

        when(categoriaService.obtenerCategoriaPorID(1L))
                .thenReturn(categoriaModel);

        mockMvc.perform(get("/categoria/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Categoria 1"));

        verify(categoriaService).obtenerCategoriaPorID(1L);
    }
}
