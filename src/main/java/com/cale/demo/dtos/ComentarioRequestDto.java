package com.cale.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos necesarios para crear un nuevo comentario")
public class ComentarioRequestDto {

    @Schema(
            description = "Comentario para el post",
            example = "Este post es muy bueno"
    )
    @NotBlank
    private String comentario;

    public ComentarioRequestDto(){
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
