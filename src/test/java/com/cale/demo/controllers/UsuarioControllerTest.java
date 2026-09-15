package com.cale.demo.controllers;

import com.cale.demo.dtos.PageResponse;
import com.cale.demo.dtos.PrioridadRequest;
import com.cale.demo.dtos.UsuarioResponseDto;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.security.JwtAuthenticationFilter;
import com.cale.demo.services.PostService;
import com.cale.demo.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void obtenerUsuarioPorIdNoDebeLanzarExepcion() throws Exception {
        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(1L);
        usuarioModel.setPrioridad(3);
        usuarioModel.setNombre("Usuario");

        when(usuarioService.obtenerPorId(1L)).thenReturn(usuarioModel);

        mockMvc.perform(get("/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Usuario"));

    }

    @Test
    void obtenerUsuarioPorIdQueNoExisteDebeLanzarExepcion() throws Exception {

        when(usuarioService.obtenerPorId(99L)).thenThrow(new RecursoNoEncontradoException("Post no encontrado"));

        mockMvc.perform(get("/usuario/99"))
                .andExpect(status().isNotFound());

    }

    @Test
    void obtenerUsuariosNoDebeLanzarExepcion() throws Exception {
        UsuarioResponseDto  usuarioResponseDto = new UsuarioResponseDto();
        usuarioResponseDto.setId(1L);
        usuarioResponseDto.setNombre("Usuario");

        Pageable pageable = PageRequest.of(0, 1);
        Page<UsuarioResponseDto> pagina =
                new PageImpl<>(List.of(usuarioResponseDto), pageable, 1);

        PageResponse<UsuarioResponseDto> pageResponse = new PageResponse<>(List.of(usuarioResponseDto),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages()
                );

        when(usuarioService.obtenerUsuarios(any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido[0].id").value(1))
                .andExpect(jsonPath("$.contenido[0].nombre").value("Usuario"));

    }

    @Test
    void obtenerUsuarioPorPrioridadNoDebeLanzarExepcion() throws Exception {
        UsuarioResponseDto usuarioResponseDto = new UsuarioResponseDto();
        usuarioResponseDto.setId(1L);
        usuarioResponseDto.setNombre("Usuario");


        when(usuarioService.obtenerUsuariosPorPrioridad(3)).thenReturn(List.of(usuarioResponseDto));

        mockMvc.perform(get("/usuario/query?prioridad=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Usuario"));

    }

    @Test
    void obtenerUsuarioPorPrioridadDebeLanzarExepcionSiNoExiste() throws Exception {

        when(usuarioService.obtenerUsuariosPorPrioridad(99)).thenThrow(RecursoNoEncontradoException.class);

        mockMvc.perform(get("/usuario/query?prioridad=99"))
                .andExpect(status().isNotFound());

    }

    @Test
    void eliminarUsuarioPorIdNoDebeLanzarExepcion() throws Exception {
        doNothing().when(usuarioService).eliminarUsuario(1L);

        mockMvc.perform(delete("/usuario/1"))
                .andExpect(status().isNoContent());

    }

    @Test
    void eliminarUsuarioPorIdDebeLanzarExepcionSiNoExiste() throws Exception {

        doThrow(new RecursoNoEncontradoException("Usuario no encontrado"))
                .when(usuarioService).eliminarUsuario(99L);

        mockMvc.perform(delete("/usuario/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarPrioridadDeUsuarioDebeDevolverUsuario() throws Exception {

        UsuarioResponseDto usuarioResponseDto = new UsuarioResponseDto();
        usuarioResponseDto.setId(1L);
        usuarioResponseDto.setNombre("Usuario");

        String prioridadBody = """
                {
                    "prioridad":1
                }
                """;

        when(usuarioService.actualizarPrioridad(eq(1L),
                any(PrioridadRequest.class))).thenReturn(usuarioResponseDto);

        mockMvc.perform(patch("/usuario/1/prioridad")
                .contentType(MediaType.APPLICATION_JSON)
                .content(prioridadBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Usuario"));
    }
}
