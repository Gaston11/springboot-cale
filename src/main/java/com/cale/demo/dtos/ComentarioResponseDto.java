package com.cale.demo.dtos;

import java.util.Set;

public class ComentarioResponseDto {
    private String comentario;
    private Long id;

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
