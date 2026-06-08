package com.cale.demo.controllers;

import com.cale.demo.dtos.PageResponse;
import com.cale.demo.dtos.RegisterRequest;
import com.cale.demo.dtos.UsuarioResponseDto;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    @Autowired //para no crear la instancia nueva
    UsuarioService usuarioService;

    @GetMapping
    public PageResponse<UsuarioResponseDto> obtenerUsuarios(Pageable pageable) {
        return usuarioService.obtenerUsuarios(pageable);
    }

    @PostMapping
    public UsuarioModel guardarUsuario(@RequestBody UsuarioModel usuarioModel){
        return usuarioService.guardarUsuario(usuarioModel);
    }

    @GetMapping(path = "/{id}")
    public UsuarioModel obtenerUsuarioPorId(@PathVariable("id") Long id){
        return usuarioService.obtenerPorId(id);
    }

    @GetMapping(path = "/query")
    public ArrayList<UsuarioModel> obtenerUsuarioPorPrioridad(@RequestParam("prioridad") Integer prioridad){
        return usuarioService.obtenerUsuariosPorPrioridad(prioridad);
    }

    @DeleteMapping(path = "/{id}")
    public void eliminarUsuarioPorId(@PathVariable("id") Long id){
        usuarioService.eliminarUsuario(id);
    }

}
