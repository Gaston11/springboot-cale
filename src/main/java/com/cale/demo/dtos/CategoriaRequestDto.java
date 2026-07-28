package com.cale.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos necesarios para crear una nueva categoria")
public class CategoriaRequestDto {

    @Schema(
            description = "Nombre de la categoria",
            example = "Lenguajes"
    )
    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
