package com.cale.demo.exepciones;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorResponseTest {

    @Test
    void debePermitirModificarSusValores() {

        ErrorResponse errorResponse = new ErrorResponse(
                "Error inicial",
                400,
                LocalDateTime.of(2026, 1, 1, 10, 0)
        );

        LocalDateTime nuevoTimestamp =
                LocalDateTime.of(2026, 2, 2, 15, 30);

        errorResponse.setMensaje("Nuevo mensaje");
        errorResponse.setStatus(500);
        errorResponse.setTimestamp(nuevoTimestamp);

        assertEquals("Nuevo mensaje", errorResponse.getMensaje());
        assertEquals(500, errorResponse.getStatus());
        assertEquals(nuevoTimestamp, errorResponse.getTimestamp());
    }
}