package com.cale.demo.exepciones;

public class NoAutorizadoException extends RuntimeException{
    public NoAutorizadoException(String mensaje){
        super(mensaje);
    }


}
