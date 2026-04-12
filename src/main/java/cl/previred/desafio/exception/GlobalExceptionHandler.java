package cl.previred.desafio.exception;

import cl.previred.desafio.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Manejador centralizado de excepciones para toda la aplicacion.
 *
 * <p>Esta clase actua como un interceptor global para excepciones no controladas,
 * asegurando que todas las respuestas de error sean consistentes y esten
 * formateadas como {@link ErrorResponse}.</p>
 *
 * <p>Utiliza {@link ApiExceptionResolver} internamente para determinar
 * el codigo de estado HTTP y el cuerpo de la respuesta apropiados.</p>
 *
 * @see ApiExceptionResolver
 * @see ErrorResponse
 * @since 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** Logger para trazabilidad de errores no controlados. */
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Resolvedor de excepciones especificas de la API. */
    private final ApiExceptionResolver apiExceptionResolver;

    /**
     * Constructor con dependencias inyectadas.
     *
     * @param apiExceptionResolver resolvedor de excepciones
     */
    public GlobalExceptionHandler(ApiExceptionResolver apiExceptionResolver) {
        this.apiExceptionResolver = apiExceptionResolver;
    }

    /**
     * Maneja cualquier excepcion no capturada que ocurra durante el procesamiento
     * de una peticion HTTP.
     *
     * <p>Este metodo es el ultimo recurso para manejo de excepciones.
     * Delega la resolucion a {@link ApiExceptionResolver} para determinar
     * el tipo de error y generar la respuesta apropiada.</p>
     *
     * @param ex      la excepcion queoccurrio
     * @param request la peticion HTTP que causo la excepcion
     * @return ResponseEntity con el ErrorResponse y codigo de estado correspondiente
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAnyException(Exception ex, HttpServletRequest request) {
        ResolvedErrorResponse resolved = apiExceptionResolver.resolve(ex, request.getRequestURI());
        return ResponseEntity.status(resolved.getStatus()).body(resolved.getBody());
    }
}