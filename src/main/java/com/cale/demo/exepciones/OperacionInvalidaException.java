package com.cale.demo.exepciones;

public class OperacionInvalidaException extends RuntimeException{
    public OperacionInvalidaException(String mensaje){
        super(mensaje);
    }
}
