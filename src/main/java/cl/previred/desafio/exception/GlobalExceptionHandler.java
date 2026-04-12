package cl.previred.desafio.exception;

import cl.previred.desafio.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Manejador centralizado de excepciones para controladores Spring MVC.
 *
 * <p>Esta clase actua como un interceptor global via {@code @ControllerAdvice}
 * para excepciones no controladas que ocurren en endpoints Spring MVC,
 * asegurando que todas las respuestas de error sean consistentes y esten
 * formateadas como {@link ErrorResponse}.</p>
 *
 * <p><strong>Rol arquitectonico:</strong> Este handler esta diseñado exclusivamente
 * para el pipeline de Spring MVC (controladores REST anotados con
 * {@code @RestController}). No reemplaza el manejo explicito requerido por
 * {@link javax.servlet.http.HttpServlet} clasicos como {@link cl.previred.desafio.servlet.EmpleadoServlet},
 * los cuales utilizan {@link ApiExceptionResolver} directamente.</p>
 *
 * <p>Ambos mecanismos ({@code GlobalExceptionHandler} y el manejo directo del servlet)
 * reutilizan {@link ApiExceptionResolver} internamente para compartir el mismo
 * contrato de errores.</p>
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