package com.cale.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categoria")
public class CategoriaModel extends AuditableModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @NotNull
    @NotBlank
    private String nombre;

    @ManyToMany(mappedBy = "categorias")
    @JsonIgnore
    private Set<PostModel> posts = new HashSet<>();

    public CategoriaModel() {
    }

    public Set<PostModel> getPosts() {
        return posts;
    }

    public void setPosts(Set<PostModel> posts) {
        this.posts = posts;
    }

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
}
