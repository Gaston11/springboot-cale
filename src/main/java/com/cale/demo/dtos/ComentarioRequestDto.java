package com.cale.demo.dtos;

import jakarta.validation.constraints.NotBlank;

public class ComentarioRequestDto {

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
