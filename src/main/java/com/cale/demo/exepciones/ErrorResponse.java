package com.cale.demo.exepciones;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta estándar de error")
public class ErrorResponse {
    @Schema(
            description = "Descripción del error",
            example = "Post no encontrado"
    )
    private String mensaje;

    @Schema(
            description = "Código HTTP",
            example = "404"
    )
    private int status;

    @Schema(
            description = "Fecha y hora del error",
            example = "2026-07-29T15:30:10"
    )
    private LocalDateTime timestamp;

    public ErrorResponse(String mensaje, int status,  LocalDateTime timestamp) {
        this.mensaje = mensaje;
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
