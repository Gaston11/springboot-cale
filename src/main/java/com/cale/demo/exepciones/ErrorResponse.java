package com.cale.demo.exepciones;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String mensaje;
    private int status;
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
