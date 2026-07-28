package com.cale.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta exitosa de creación de usuario")
public class UsuarioResponseDto {

    @Schema(
            description = "Valor unico de identificador del usuario",
            example = "13"
    )
    private Long id;

    @Schema(
            description = "Nombre del usuario",
            example = "Gastón"
    )
    private String nombre;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
