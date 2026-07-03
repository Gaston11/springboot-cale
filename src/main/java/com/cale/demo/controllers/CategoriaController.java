package com.cale.demo.controllers;

import com.cale.demo.models.CategoriaModel;
import com.cale.demo.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ArrayList<CategoriaModel> obetenerCategorias(){
        return categoriaService.obetenerCategorias();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaModel guardarCategoria(@RequestBody CategoriaModel categoriaModel){
        return categoriaService.guardarCategoria(categoriaModel);
    }

    @GetMapping(path = "/{id}")
    public Optional<CategoriaModel> obtenerCategoriaPorID(@PathVariable("id") Long id){
        return categoriaService.obtenerCategoriaPorID(id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarCategoria(@PathVariable("id") Long id){
        categoriaService.eliminarCategoria(id);
    }

}
