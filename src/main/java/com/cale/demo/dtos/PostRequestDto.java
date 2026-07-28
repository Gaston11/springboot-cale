package com.cale.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

@Schema(description = "Datos necesarios para crear un nuevo post o actulizar")
public class PostRequestDto {

    @Schema(
            description = "Título del post",
            example = "Introducción a Java"
    )
    @NotBlank
    private String titulo;

    @Schema(
            description = "Contenido o descripción del nuevo post",
            example = "En este artículo veremos contenido de java"
    )
    @NotBlank
    private String descripcion;

    @Schema(
            description = "Ids de las categorias asociadas al post",
            example = "[1, 2]"
    )
    @NotNull
    private Set<Long> categoriaIds;

    public Set<Long> getCategoriaIds() {
        return categoriaIds;
    }

    public void setCategoriaIds(Set<Long> categoriaIds) {
        this.categoriaIds = categoriaIds;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
