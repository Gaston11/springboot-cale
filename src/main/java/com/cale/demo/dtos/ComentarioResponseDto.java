package com.cale.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta de Comentario exitoso")
public class ComentarioResponseDto {

    @Schema(
            description = "Comentario nuevo",
            example = "Muy bueno el post"
    )
    private String comentario;

    @Schema(
            description = "Identificador único del comentario",
            example = "10"
    )
    private Long id;

    @Schema(
            description = "Fecha de creación",
            example = "2026-06-23T15:30:20"
    )
    private LocalDateTime fechaCreacion;

    @Schema(
            description = "Fecha de actualización",
            example = "2026-06-23T15:30:20"
    )
    private LocalDateTime fechaActualizacion;

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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
