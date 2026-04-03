package com.cale.demo.dtos;

import java.util.Set;

public class PostResponseDto {

    private Long id;
    private String titulo;
    private String descripcion;
    private String nombreUsuario;
    private Set<String> nombreCategorias;

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

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Set<String> getNombreCategorias() {
        return nombreCategorias;
    }

    public void setNombreCategorias(Set<String> nombreCategorias) {
        this.nombreCategorias = nombreCategorias;
    }
}
