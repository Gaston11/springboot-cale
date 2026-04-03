package com.cale.demo.services;

import com.cale.demo.dtos.PostRequestDto;
import com.cale.demo.dtos.PostResponseDto;
import com.cale.demo.exepciones.RecursoNoEncontradoExepcion;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.models.PostModel;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.CategoriaRepository;
import com.cale.demo.repositories.PostRepository;
import com.cale.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private final PostRepository postRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public PostService(UsuarioRepository usuarioRepository, PostRepository postRepository, CategoriaRepository categoriaRepository) {
        this.postRepository = postRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ArrayList<PostModel> obtenerPosts() {
        return (ArrayList<PostModel>) postRepository.findAll();
    }

    public PostResponseDto guardarPost(PostRequestDto postRequestDto) {
        PostModel postModel = new PostModel();
        postModel.setTitulo(postRequestDto.getTitulo());
        postModel.setDescripcion(postRequestDto.getDescripcion());

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel = usuarioRepository.findById(postRequestDto.getUsuarioId())
                .orElseThrow( ()-> new RuntimeException("Usuario no encontrado con id " + postRequestDto.getUsuarioId()));

        postModel.setUsuario(usuarioModel);

        Set<CategoriaModel> categorias = new HashSet<>((Collection) categoriaRepository.findAllById(postRequestDto.getCategoriasId()));

        postModel.setCategorias(categorias);

        return convertirADto(postRepository.save(postModel));
    }

    private PostResponseDto convertirADto(PostModel postModel) {
        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(postModel.getId());
        postResponseDto.setTitulo(postModel.getTitulo());
        postResponseDto.setDescripcion(postModel.getDescripcion());
        postResponseDto.setNombreUsuario(postModel.getUsuario().getNombre());
        postResponseDto.setNombreCategorias(postModel.getCategorias()
        .stream().map(c -> c.getNombre()).collect(Collectors.toSet()));

        return postResponseDto;
    }

    public PostModel obtenerPostPorID(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoExepcion("Post no encontrado con ID: " + id));
    }

    public void eliminarPost(Long id) {
        if (obtenerPostPorID(id) != null){
            postRepository.deleteById(id);
        }
    }
}
