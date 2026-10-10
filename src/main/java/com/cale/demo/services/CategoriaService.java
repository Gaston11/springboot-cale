package com.cale.demo.services;

import com.cale.demo.exepciones.NoAutorizadoException;
import com.cale.demo.exepciones.RecursoNoEncontradoException;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.models.Rol;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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
        UsuarioModel usuarioModel = this.currentUserService.getCurrentUser();
        if (usuarioModel.getRol() != Rol.ADMIN){
            throw new NoAutorizadoException("No puedes crear categoria");
        }
        return categoriaRepository.save(categoriaModel);
    }

    public CategoriaModel obtenerCategoriaPorID(Long id) {
        return categoriaRepository.findById(id).
                orElseThrow(() ->
                        new RecursoNoEncontradoException("Categoria no encontrada"));
    }

    public void eliminarCategoria(Long id) {
        this.obtenerCategoriaPorID(id);
        UsuarioModel usuarioModel = this.currentUserService.getCurrentUser();
        if (usuarioModel.getRol() != Rol.ADMIN){
            throw new NoAutorizadoException("No puedes eliminar categoria");
        }
        categoriaRepository.deleteById(id);
    }
}
