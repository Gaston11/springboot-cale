package com.cale.demo.integrationTest;

import com.cale.demo.dtos.PrioridadRequest;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UsuarioIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void buscarUsuarios() throws Exception {
        String token = obtenerToken("demo@cale.com","demo1234");

        mockMvc.perform(get("/usuario")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void buscarUsuarioPorPrioridad() throws Exception {
        String token = obtenerToken("demo@cale.com","demo1234");

        mockMvc.perform(get("/usuario/query?prioridad=10")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Usuario"));
    }

    @Test
    void buscarUsuarioPorId() throws Exception {
        String token = obtenerToken("demo@cale.com","demo1234");
        Long id = obtenerIdPorEmail("demo@cale.com");

        mockMvc.perform(get("/usuario/{id}",id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Usuario"));
    }

    @Test
    void adminPuedeEliminarUsuario() throws Exception {
        crearUsuarioAdmin("admin@admin.com");
        registrarUsuario("prueba@prueba.com");
        Long id = obtenerIdPorEmail("prueba@prueba.com");
        String token = obtenerToken("admin@admin.com","123456");

        mockMvc.perform(get("/usuario/{id}",id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/usuario/{id}",id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/usuario/{id}",id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void usuarioConRolUserNoPuedeModificarPrioridad() throws Exception {
        String token = obtenerToken("demo@cale.com","demo1234");
        Long id = this.usuarioRepository.findByEmail("demo@cale.com").get().getId();
        PrioridadRequest prioridadRequest = new PrioridadRequest();
        prioridadRequest.setPrioridad(1);

        mockMvc.perform(patch("/usuario/{id}/prioridad",id)
                        .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(prioridadRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void usuarioConRolAdminPuedeModificarPrioridad() throws Exception {
        registrarUsuario("pruebaPrioridad@mail.com");
        crearUsuarioAdmin("admin@cale.com");
        String token = obtenerToken("admin@cale.com","123456");
        Long id = this.usuarioRepository.findByEmail("pruebaPrioridad@mail.com").get().getId();
        PrioridadRequest prioridadRequest = new PrioridadRequest();
        prioridadRequest.setPrioridad(1);

        mockMvc.perform(get("/usuario/{id}",id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/usuario/{id}/prioridad",id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prioridadRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/usuario/{id}",id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        UsuarioModel usuarioActualizado =
                usuarioRepository.findById(id).orElseThrow();

        Assertions.assertEquals(1, usuarioActualizado.getPrioridad());
    }

    @Test
    void buscarUsuarioPorIdInexistenteDevuelve404() throws Exception {
        String token = obtenerToken("demo@cale.com", "demo1234");

        mockMvc.perform(get("/usuario/{id}", 999999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarUsuarioPorPrioridadInexistenteDevuelve404() throws Exception {
        String token = obtenerToken("demo@cale.com", "demo1234");

        mockMvc.perform(get("/usuario/query?prioridad=999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void usuarioNoPuedeEliminarUsuario() throws Exception {
        registrarUsuario("user@user.com");
        registrarUsuario("prueba@prueba.com");
        Long id = obtenerIdPorEmail("prueba@prueba.com");
        String token = obtenerToken("user@user.com","123456");

        mockMvc.perform(get("/usuario/{id}",id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/usuario/{id}",id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/usuario/{id}",id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

}
