package cl.previred.desafio.exception;

import cl.previred.desafio.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ApiExceptionResolver apiExceptionResolver;

    public GlobalExceptionHandler(ApiExceptionResolver apiExceptionResolver) {
        this.apiExceptionResolver = apiExceptionResolver;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAnyException(Exception ex, HttpServletRequest request) {
        ResolvedErrorResponse resolved = apiExceptionResolver.resolve(ex, request.getRequestURI());
        return ResponseEntity.status(resolved.getStatus()).body(resolved.getBody());
    }
}