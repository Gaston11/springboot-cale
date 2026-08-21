package com.cale.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Prioridad para cambiar a usuario")
public class PrioridadRequest {

    @Schema(
            description = "Prioridad del usuario",
            example = "10"
    )
    @NotBlank
    private Integer prioridad;

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }
}
