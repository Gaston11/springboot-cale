package com.cale.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class PostRequestDto {

    @NotBlank
    private String titulo;
    @NotBlank
    private String descripcion;

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
