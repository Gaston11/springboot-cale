package com.cale.demo.exepciones;

public class RecursoYaExisteException extends RuntimeException {
    public RecursoYaExisteException(String message) {
        super(message);
    }
}
