package com.cale.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta del login exitoso")
public class LoginResponse {

    @Schema(
            description = "Token JWT para autenticar las siguientes peticiones",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    String token;

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
