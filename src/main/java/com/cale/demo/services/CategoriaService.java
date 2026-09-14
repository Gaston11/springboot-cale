package com.cale.demo.services;

import com.cale.demo.exepciones.NoAutorizadoException;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;
    private final CurrentUserService currentUserService;

    public CategoriaService(CategoriaRepository categoriaRepository, CurrentUserService currentUserService) {
        this.categoriaRepository = categoriaRepository;
        this.currentUserService = currentUserService;
    }

    public List<CategoriaModel> obtenerCategorias() {
        return categoriaRepository.findAll();
    }

    public CategoriaModel guardarCategoria(CategoriaModel categoriaModel) {
        return categoriaRepository.save(categoriaModel);
    }

    public CategoriaModel obtenerCategoriaPorID(Long id) {
        return categoriaRepository.findById(id).
                orElseThrow(() ->
                        new RecursoNoEncontradoException("Categoria no encontrada"));
    }

    public void eliminarCategoria(Long id) {
        this.obtenerCategoriaPorID(id);
        categoriaRepository.deleteById(id);
    }
}
