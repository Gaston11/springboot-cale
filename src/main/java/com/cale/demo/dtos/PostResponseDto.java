package com.cale.demo.dtos;

import java.time.LocalDateTime;
import java.util.Set;

public class PostResponseDto {

    private Long id;
    private String titulo;
    private String descripcion;
    private UsuarioResponseDto usuario;
    private Set<String> nombreCategorias;
    private LocalDateTime fechaCreacion;
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
