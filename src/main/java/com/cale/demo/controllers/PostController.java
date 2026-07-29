package com.cale.demo.controllers;

import com.cale.demo.dtos.*;
import com.cale.demo.exepciones.ErrorResponse;
import com.cale.demo.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/post")
@Tag(
        name = "Post",
        description = "Endpoints para los post y comentarios de estos."
)
@SecurityRequirement(name = "bearerAuth")
public class PostController {

    @Autowired
    private PostService postService;

    @Operation(
            summary = "Obtener posts",
            description = "Devuelve los posts por pagina."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Post encontrados",
                    content = @Content(schema = @Schema(implementation = PostResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Posts no encontrados",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    public PageResponse<PostResponseDto> obtenerPosts(
            @ParameterObject
            @PageableDefault(page = 0, size = 10)
            Pageable pageable,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) Long usuarioId
            ) {
        return postService.obtenerPosts(pageable, titulo, usuarioId);
    }

    @Operation(
            summary = "Crear un nuevo post",
            description = "Crea un nuevo post con los parametros indicados"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Post encontrados",
                    content = @Content(schema = @Schema(implementation = PostResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Post no formado correctamente",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseDto guardarPost(@Valid @RequestBody PostRequestDto postRequestDto) {
        return postService.guardarPost(postRequestDto);
    }

    @Operation(
            summary = "Obtener un post por ID",
            description = "Devuelve un post existente junto con su usuario y categorías."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Post encontrado",
                    content = @Content(schema = @Schema(implementation = PostResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping(path = "/{id}")
    public PostResponseDto obtenerPostPorID(@PathVariable("id") Long id){
        return postService.obtenerPostPorID(id);
    }

    @Operation(
            summary = "Eliminar un post por ID",
            description = "Elimina un post indicando el ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Post eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPost(@PathVariable("id") Long id){
        postService.eliminarPost(id);
    }

    @Operation(
            summary = "Editar post",
            description = "Editar un post indicando el ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Post Editado correctamente",
                    content = @Content(schema = @Schema(implementation = PostResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autorizado para editar el Post",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponseDto actualizarPost( @PathVariable("id") Long id, @Valid @RequestBody PostRequestDto postRequestDto) {
        return postService.actualizarPost(postRequestDto,id);
    }

    @Operation(
            summary = "Crear comentario",
            description = "Crear un comentario indicando el ID del post"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comentario creado correctamente",
                    content = @Content(schema = @Schema(implementation = ComentarioResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{id}/comentarios")
    public ComentarioResponseDto guardarComentario(@PathVariable("id")  Long id, @Valid @RequestBody ComentarioRequestDto comentarioRequestDto) {
        return this.postService.guardarComentario(id,comentarioRequestDto);
    }

    @Operation(
            summary = "Obtener comentarios",
            description = "Obtener lista de comentarios del post indicando el ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de comentarios del post",
                    content = @Content(schema = @Schema(implementation = ComentarioResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
    })
    @GetMapping(path = "/{id}/comentarios")
    public List<ComentarioResponseDto> obtenerComentarios(@PathVariable("id")  Long id){
        return this.postService.obtenerComentarios(id);
    }
}
