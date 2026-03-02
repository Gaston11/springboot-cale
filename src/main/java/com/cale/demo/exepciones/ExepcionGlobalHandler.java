package com.cale.demo.exepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExepcionGlobalHandler {
    @ExceptionHandler(RecursoNoEncontradoExepcion.class)
    public ResponseEntity<RespuestaError> manejarRecursoNoEncontrado(RecursoNoEncontradoExepcion exception) {
        RespuestaError respuestaError = new RespuestaError(exception.getMessage(), HttpStatus.NOT_FOUND.value());
        return  new ResponseEntity<>(respuestaError, HttpStatus.NOT_FOUND);
    }
}
