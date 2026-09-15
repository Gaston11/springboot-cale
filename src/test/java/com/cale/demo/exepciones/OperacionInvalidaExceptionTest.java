package com.cale.demo.exepciones;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OperacionInvalidaExceptionTest {

    @Test
    void debeCrearExcepcionConElMensajeIndicado() {
        OperacionInvalidaException exception =
                assertThrows(
                        OperacionInvalidaException.class,
                        () -> {
                            throw new OperacionInvalidaException("Operación inválida");
                        }
                );

        assertEquals("Operación inválida", exception.getMessage());
    }
}