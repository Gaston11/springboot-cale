package com.cale.demo.exepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarRecursoNoEncontradoException(RecursoNoEncontradoException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(NoAutorizadoException.class)
    public ResponseEntity<ErrorResponse> manejarNoAutorizadoException(
            NoAutorizadoException ex) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }

    @ExceptionHandler(OperacionInvalidaException.class)
    public ResponseEntity<ErrorResponse> manejarOperacionInvalidaException(
            OperacionInvalidaException ex) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> manejarCredencialesInvalidasException(
            CredencialesInvalidasException ex) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidaciones(
            MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse(
                mensaje,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(RecursoYaExisteException.class)
    public ResponseEntity<ErrorResponse> manejarRecursoYaExisteException(
            RecursoYaExisteException ex) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorInterno(Exception ex) {

        ErrorResponse error = new ErrorResponse(
                "Error interno del servidor",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> manejarRecursoWebNoEncontrado(
            NoResourceFoundException ex) {

        ErrorResponse error = new ErrorResponse(
                "Recurso no encontrado",
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }
}
