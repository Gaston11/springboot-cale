package com.cale.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos necesarios para loguear un usuario")
public class LoginRequest {

    @Schema(
            description = "Email del usuario",
            example = "gaston@mail.com"
    )
    @NotBlank
    String email;

    @Schema(
            description = "Contraseña del usuario",
            example = "123456"
    )
    @NotBlank
    String password;

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
