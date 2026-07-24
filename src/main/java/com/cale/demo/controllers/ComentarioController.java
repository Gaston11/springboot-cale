package com.cale.demo.controllers;

import com.cale.demo.dtos.ComentarioRequestDto;
import com.cale.demo.dtos.ComentarioResponseDto;
import com.cale.demo.models.ComentarioModel;
import com.cale.demo.services.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarComentario(@PathVariable long id) {
        this.comentarioService.eliminarComentario(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ComentarioResponseDto editarComentario(@PathVariable long id, @Valid @RequestBody ComentarioRequestDto comentarioRequestDto) {
        return this.comentarioService.editarComentario(id,comentarioRequestDto);
    }
}
