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
    private Set<Long> categoriasIds;

    public Set<Long> getCategoriasId() {
        return categoriasIds;
    }

    public void setCategoriasIds(Set<Long> categoriasId) {
        this.categoriasIds = categoriasId;
    }

    /*
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

     */

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
