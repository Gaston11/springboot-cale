package com.cale.demo.dtos;

import java.util.Set;

public class UsuarioRequestDto {

    private String nombre;
    private String apellido;
    private Integer prioridad;
    private String email;
    private Set<Long> postsIds;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Long> getPostsIds() {
        return postsIds;
    }

    public void setPostsIds(Set<Long> postsIds) {
        this.postsIds = postsIds;
    }
}
