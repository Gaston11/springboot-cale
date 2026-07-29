package com.cale.demo.controllers;

import com.cale.demo.dtos.PostResponseDto;
import com.cale.demo.exepciones.ErrorResponse;
import com.cale.demo.models.CategoriaModel;
import com.cale.demo.services.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/categoria")
@Tag(
        name = "Categoria",
        description = "Endpoints para registro de categorias."
)
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Operation(
            summary = "Obtiene todas las categorias",
            description = "Obtiene todas las categorias"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categorias encontradas",
                    content = @Content(schema = @Schema(implementation = CategoriaModel.class))
            ),
    })
    @GetMapping
    public ArrayList<CategoriaModel> obetenerCategorias(){
        return categoriaService.obetenerCategorias();
    }

    @Operation(
            summary = "Guardar nueva categoria",
            description = "Crea una nueva categoria con los parametros indicados"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Categoria creada",
                    content = @Content(schema = @Schema(implementation = CategoriaModel.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Error al crear Categoria.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaModel guardarCategoria(@RequestBody CategoriaModel categoriaModel){
        return categoriaService.guardarCategoria(categoriaModel);
    }

    @Operation(
            summary = "Obtener categoria por ID",
            description = "Encuentra categoria por ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria encontrada",
                    content = @Content(schema = @Schema(implementation = CategoriaModel.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoria no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping(path = "/{id}")
    public Optional<CategoriaModel> obtenerCategoriaPorID(@PathVariable("id") Long id){
        return categoriaService.obtenerCategoriaPorID(id);
    }

    @Operation(
            summary = "Eliminar categoria",
            description = "Elimina categoria por ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Categoria eliminada"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoria no encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarCategoria(@PathVariable("id") Long id){
        categoriaService.eliminarCategoria(id);
    }

}
