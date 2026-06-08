package com.cale.demo.exepciones;

public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje){
        super(mensaje);
    }
}
