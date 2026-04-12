package com.empleado.app.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones de la API.
 * Intercepta errores lanzados en los controladores y los convierte en respuestas HTTP consistentes.
 * Autor: Cristian Palacios
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones de tipo ApiException.
     * Retorna una respuesta HTTP 400 (Bad Request) con la lista de errores.
     *
     * @param ex Excepción personalizada con los errores de negocio o validación.
     * @return ResponseEntity con estructura JSON de errores.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handle(ApiException ex) {

        // Se construye una respuesta estándar de error
        return ResponseEntity.badRequest().body(
                Map.of("errors", ex.getErrors())
        );
    }
}