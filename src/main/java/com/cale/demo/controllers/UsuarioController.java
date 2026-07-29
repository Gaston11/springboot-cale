package com.cale.demo.controllers;

import com.cale.demo.dtos.PageResponse;
import com.cale.demo.dtos.PostResponseDto;
import com.cale.demo.dtos.RegisterRequest;
import com.cale.demo.dtos.UsuarioResponseDto;
import com.cale.demo.exepciones.ErrorResponse;
import com.cale.demo.models.UsuarioModel;
import com.cale.demo.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/usuario")
@Tag(
        name = "Usuario",
        description = "Endpoints para Usuarios."
)
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {
    @Autowired //para no crear la instancia nueva
    UsuarioService usuarioService;

    @Operation(
            summary = "Obtener usuarios",
            description = "Devuelve los usuarios por pagina."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuarios encontrados",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))
            ),
    })
    @GetMapping
    public PageResponse<UsuarioResponseDto> obtenerUsuarios(Pageable pageable) {
        return usuarioService.obtenerUsuarios(pageable);
    }

    @Operation(
            summary = "Crear usuario",
            description = "Crear un nuevo usuario con los parametros indicados"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario creado",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuarios no creado, paramentros incorrectos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    //Revisar, o esta deprecado
    @PostMapping
    public UsuarioModel guardarUsuario(@RequestBody UsuarioModel usuarioModel){
        return usuarioService.guardarUsuario(usuarioModel);
    }

    @Operation(
            summary = "Obtener usuario por ID",
            description = "Devuelve el usuario encontrado"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping(path = "/{id}")
    public UsuarioModel obtenerUsuarioPorId(@PathVariable("id") Long id){
        return usuarioService.obtenerPorId(id);
    }

    @Operation(
            summary = "Obtener usuarios por prioridad",
            description = "Devuelve los usuarios encontrados por prioridad."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuarios encontrados",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuarios no encontrados",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping(path = "/query")
    public ArrayList<UsuarioModel> obtenerUsuarioPorPrioridad(@RequestParam("prioridad") Integer prioridad){
        return usuarioService.obtenerUsuariosPorPrioridad(prioridad);
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina el usuario encontrado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping(path = "/{id}")
    public void eliminarUsuarioPorId(@PathVariable("id") Long id){
        usuarioService.eliminarUsuario(id);
    }

}
