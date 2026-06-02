package com.cale.demo.dtos;

import java.util.Set;

public class PostResponseDto {

    private Long id;
    private String titulo;
    private String descripcion;
    private UsuarioResponseDto usuario;
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

    public UsuarioResponseDto getUsuarioDto() {
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
}
