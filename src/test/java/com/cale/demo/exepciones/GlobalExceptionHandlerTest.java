package com.cale.demo.exepciones;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @Test
    void manejarOperacionInvalidaDebeRetornar400() {

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        OperacionInvalidaException exception =
                new OperacionInvalidaException("Operación inválida");

        ResponseEntity<ErrorResponse> response =
                handler.manejarOperacionInvalidaException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Operación inválida", response.getBody().getMensaje());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void manejarErrorInternoDebeRetornar500() {

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        Exception exception = new Exception("Error de prueba");

        ResponseEntity<ErrorResponse> response =
                handler.manejarErrorInterno(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno del servidor", response.getBody().getMensaje());
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    void manejarRecursoWebNoEncontradoDebeRetornar404() {

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        NoResourceFoundException exception =
                new NoResourceFoundException(
                        HttpMethod.GET,
                        "/ruta-inexistente"
                );

        ResponseEntity<ErrorResponse> response =
                handler.manejarRecursoWebNoEncontrado(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Recurso no encontrado", response.getBody().getMensaje());
        assertEquals(404, response.getBody().getStatus());
    }
}