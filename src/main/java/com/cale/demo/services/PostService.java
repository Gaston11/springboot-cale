package com.cale.demo.services;

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
import java.util.HashSet;
import java.util.Set;

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

    public PostModel guardarPost(PostModel postModel) {
        Set<CategoriaModel> categoriasFinales = new HashSet<>();

        if (postModel.getUsuario() == null || postModel.getUsuario().getId() == null) {
            throw new RuntimeException("El usuario es obligatorio");
        }

        UsuarioModel usuarioBD = usuarioRepository.findById(postModel.getUsuario().getId())
                .orElseThrow(() -> new RecursoNoEncontradoExepcion(
                        "Usuario no encontrado con ID: " + postModel.getUsuario().getId()
                ));

        if (postModel.getCategorias() != null && !postModel.getCategorias().isEmpty()) {
            for (CategoriaModel categoria : postModel.getCategorias()) {
                if (categoria.getId() != null) {
                    CategoriaModel categoriaBD = categoriaRepository.findById(categoria.getId())
                            .orElseThrow(() -> new RecursoNoEncontradoExepcion(
                                    "Categoría no encontrada con ID: " + categoria.getId()
                            ));
                    categoriasFinales.add(categoriaBD);
                }
            }
        }

        postModel.setUsuario(usuarioBD);
        postModel.setCategorias(categoriasFinales);
        return postRepository.save(postModel);
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
