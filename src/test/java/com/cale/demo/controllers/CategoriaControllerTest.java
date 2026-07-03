package com.cale.demo.controllers;

import com.cale.demo.dtos.CategoriaRequestDto;
import com.cale.demo.dtos.ComentarioResponseDto;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.security.JwtAuthenticationFilter;
import com.cale.demo.services.AuthService;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

        when(categoriaService.obetenerCategorias()).thenReturn(categoriaModels);

        mockMvc.perform(get("/categoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Categoria 1"));

        verify(categoriaService).obetenerCategorias();
    }

    @Test
    void guardarCategoriaRetorna201() throws Exception{
        CategoriaRequestDto categoriaRequestDto = new CategoriaRequestDto();
        categoriaRequestDto.setNombre("Categoria 1");

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
    void obtenerCategoriaRetorna400SiNoExiste() throws Exception{

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

}
