package com.cale.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesarios para registrar un nuevo usuario")
public class RegisterRequest {

    @Schema(
            description = "Nombre del usuario",
            example = "Gastón"
    )
    @NotBlank
    @Size(min = 2, max = 50)
    private String nombre;

    @Schema(
            description = "Correo electrónico del usuario",
            example = "gaston@mail.com"
    )
    @NotBlank
    @Email(message = "El email no tiene un formato válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @Schema(
            description = "Contraseña del usuario",
            example = "123456",
            minLength = 6
    )
    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @Schema(
            description = "Apellido del usuario",
            example = "Pérez"
    )
    @NotBlank
    @Size(min = 2, max = 50)
    private String apellido;

    @Schema(
            description = "Prioridad del usuario",
            example = "1",
            minimum = "1",
            maximum = "10"
    )
    @NotNull
    @Min(1)
    @Max(10)
    private Integer prioridad;

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
