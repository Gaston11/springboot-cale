package com.cale.demo.services;

import com.cale.demo.models.CategoriaModel;
import com.cale.demo.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;

    public ArrayList<CategoriaModel> obetenerCategorias() {
        return (ArrayList<CategoriaModel>) categoriaRepository.findAll();
    }

    public CategoriaModel guardarCategoria(CategoriaModel categoriaModel) {
        return categoriaRepository.save(categoriaModel);
    }

    public Optional<CategoriaModel> obtenerCategoriaPorID(Long id) {
        return categoriaRepository.findById(id);
    }

    public boolean eliminarCategoria(Long id) {
        try {
            categoriaRepository.deleteById(id);
            return true; //TODO revisar
        }catch(Exception e) {
            return false;
        }

    }
}
