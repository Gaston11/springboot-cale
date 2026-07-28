package com.cale.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Respuesta de post exitoso")
public class PostResponseDto {

    @Schema(
            description = "Identificador único del post",
            example = "10"
    )
    private Long id;

    @Schema(
            description = "Título del post",
            example = "Introducción a Spring Boot"
    )
    private String titulo;
    @Schema(
            description = "Descripción del post",
            example = "Conceptos básicos de Spring Boot."
    )
    private String descripcion;

    @Schema(
            description = "Datos del usuario que creo el post"
    )
    private UsuarioResponseDto usuario;

    @Schema(
            description = "Nombre de las categorias del post",
            example = "[\"Java\", \"Spring\"]"
    )
    private Set<String> nombreCategorias;

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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public UsuarioResponseDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponseDto usuario) {
        this.usuario = usuario;
    }

    public Set<String> getNombreCategorias() {
        return nombreCategorias;
    }

    public void setNombreCategorias(Set<String> nombreCategorias) {
        this.nombreCategorias = nombreCategorias;
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
