package com.cale.demo.exepciones;

public class RespuestaError {
    private String mensaje;
    private int estado;

    public RespuestaError(String mensaje, int estado) {
        this.mensaje = mensaje;
        this.estado = estado;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
