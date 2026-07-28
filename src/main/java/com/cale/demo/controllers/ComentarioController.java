package com.cale.demo.controllers;

import com.cale.demo.dtos.ComentarioRequestDto;
import com.cale.demo.dtos.ComentarioResponseDto;
import com.cale.demo.exepciones.ErrorResponse;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.services.ComentarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comentarios")
@Tag(
        name = "Comentarios",
        description = "Endpoints para editar o eliminar comentarios."
)
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @Operation(
            summary = "Eliminar comentario",
            description = "Elimina comentario indicado"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Comentario eliminado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comentario no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarComentario(@PathVariable long id) {
        this.comentarioService.eliminarComentario(id);
    }

    @Operation(
            summary = "Editar comentario",
            description = "Edita el comentario con parametros indicados"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comentario editado correctamente",
                    content = @Content(schema = @Schema(implementation = ComentarioResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comentario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ComentarioResponseDto editarComentario(@PathVariable long id, @Valid @RequestBody ComentarioRequestDto comentarioRequestDto) {
        return this.comentarioService.editarComentario(id,comentarioRequestDto);
    }
}
