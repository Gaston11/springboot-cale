package com.cale.demo.exepciones;

public class RecursoNoEncontradoExepcion extends RuntimeException {
    public RecursoNoEncontradoExepcion(String mensaje){
        super(mensaje);
    }
}
