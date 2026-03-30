package com.cale.demo.services;

import com.cale.demo.exepciones.RecursoNoEncontradoExepcion;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.models.PostModel;
import com.cale.demo.repositories.CategoriaRepository;
import com.cale.demo.repositories.PostRepository;
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

    public PostService(PostRepository postRepository, CategoriaRepository categoriaRepository) {
        this.postRepository = postRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public ArrayList<PostModel> obtenerPosts() {
        return (ArrayList<PostModel>) postRepository.findAll();
    }

    public PostModel guardarPost(PostModel postModel) {
        Set<CategoriaModel> categoriasFinales = new HashSet<>();

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
