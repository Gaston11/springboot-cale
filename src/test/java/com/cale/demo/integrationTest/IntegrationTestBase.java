package com.cale.demo.integrationTest;

import com.cale.demo.dtos.*;
import com.cale.demo.exepciones.CredencialesInvalidasException;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class IntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public String registrarYLoguear(String email) throws Exception {
        this.registrarUsuario(email);
        return this.obtenerToken(email,"123456");
    }

    public Long crearPost(String token) throws Exception {
        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitulo("Post 1");
        postRequestDto.setDescripcion("Descripcion 1");
        postRequestDto.setCategoriaIds(Set.of(crearCategoria("Java",token)));


        MvcResult postResult = mockMvc.perform(post("/post")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = postResult.getResponse().getContentAsString();
        PostResponseDto postResponseDto = objectMapper.readValue(json, PostResponseDto.class);
        return postResponseDto.getId();

    }

    public Long crearCategoria(String nombre, String token) throws Exception {
        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setNombre(nombre);

        MvcResult categoriaResult = mockMvc.perform(post("/categoria")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaModel)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = categoriaResult.getResponse().getContentAsString();
        CategoriaModel categoriaModelResponse = objectMapper.readValue(json, CategoriaModel.class);
        return categoriaModelResponse.getId();
    }

    public void registrarUsuario(String email) throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Gaston");
        request.setApellido("Perez");
        request.setEmail(email);
        request.setPassword("123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    public String obtenerToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String json = loginResult.getResponse().getContentAsString();
        LoginResponse response = objectMapper.readValue(json, LoginResponse.class);
        return response.getToken();
    }

    public Long crearUsuarioAdmin(String email) throws Exception {
        this.registrarUsuario(email);

        UsuarioModel admin = usuarioRepository
                .findByEmail(email)
                .orElseThrow();

        admin.setRol(Rol.ADMIN);
        usuarioRepository.save(admin);
        return admin.getId();
    }

    public Long obtenerIdPorEmail(String email) throws Exception {
        UsuarioModel usuarioModel = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Usuario no encontrado"));
        return usuarioModel.getId();
    }
}
