package cl.previred.desafio.exception;

import cl.previred.desafio.dto.ErrorResponse;
import cl.previred.desafio.dto.ValidationError;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador centralizado de excepciones para toda la aplicacion.
 *
 * <p>Esta clase intercepta todas las excepciones lanzadas por los
 * controllers y servlets, y las convierte en respuestas HTTP apropiadas
 * con cuerpos JSON consistentes.</p>
 *
 * <p>Maneja los siguientes tipos de excepciones:</p>
 * <ul>
 *   <li>{@link ValidationException} - Errores de validacion individuales (400)</li>
 *   <li>{@link ValidationExceptionList} - Lista de errores de validacion (400)</li>
 *   <li>{@link BusinessException} - Violaciones de reglas de negocio (400)</li>
 *   <li>{@link RepositoryException} - Errores de base de datos (500)</li>
 *   <li>{@link TechnicalException} - Errores tecnicos generales (500)</li>
 *   <li>{@link MethodArgumentTypeMismatchException} - Tipos de parametros invalidos (400)</li>
 *   <li>{@link InvalidFormatException} - Formatos JSON invalidos (400)</li>
 *   <li>{@link MethodArgumentNotValidException} - Errores de validacion Spring (400)</li>
 *   <li>{@link Exception} - Excepciones no controladas (500)</li>
 * </ul>
 *
 * <p>Todas las respuestas de error siguen el formato de {@link ErrorResponse}.</p>
 *
 * @see ErrorResponse
 * @see ValidationError
 * @see ControllerAdvice
 * @since 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** Logger para trazabilidad de excepciones. */
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de validacion individuales.
     *
     * @param ex      excepcion de validacion
     * @param request peticion HTTP que causo el error
     * @return respuesta HTTP 400 Bad Request
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex, HttpServletRequest request) {
        LOG.warn("ValidationException en {} - Campo: {}, Mensaje: {}", 
            request.getRequestURI(), ex.getCampo(), ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError(ex.getCampo(), ex.getMessage()))
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja excepciones de reglas de negocio.
     *
     * @param ex      excepcion de negocio
     * @param request peticion HTTP que causo el error
     * @return respuesta HTTP 400 Bad Request
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        LOG.warn("BusinessException en {} - Codigo: {}, Mensaje: {}", 
            request.getRequestURI(), ex.getCodigo(), ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError(ex.getCodigo(), ex.getMessage()))
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja excepciones tecnicas generales.
     *
     * <p>Nota: El mensaje de error interno no se expone al cliente
     * por razones de seguridad.</p>
     *
     * @param ex      excepcion tecnica
     * @param request peticion HTTP que causo el error
     * @return respuesta HTTP 500 Internal Server Error
     */
    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ErrorResponse> handleTechnicalException(TechnicalException ex, HttpServletRequest request) {
        LOG.error("TechnicalException en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError("internal", "Error interno del servidor"))
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Maneja excepciones de acceso a datos.
     *
     * @param ex      excepcion de repositorio
     * @param request peticion HTTP que causo el error
     * @return respuesta HTTP 500 Internal Server Error
     */
    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<ErrorResponse> handleRepositoryException(RepositoryException ex, HttpServletRequest request) {
        LOG.error("RepositoryException en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError("database", "Error interno de base de datos"))
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Maneja errores de tipo en parametros de request.
     *
     * @param ex      excepcion de tipo invalido
     * @param request peticion HTTP que causo el error
     * @return respuesta HTTP 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        LOG.warn("TypeMismatch en {} - Parametro: {}, Valor: {}", 
            request.getRequestURI(), ex.getName(), ex.getValue());
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError(ex.getName(), "Valor invalido para el parametro: " + ex.getName()))
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja errores de formato JSON.
     *
     * @param ex      excepcion de formato invalido de Jackson
     * @param request peticion HTTP que causo el error
     * @return respuesta HTTP 400 Bad Request
     */
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFormat(InvalidFormatException ex, HttpServletRequest request) {
        LOG.warn("InvalidFormat en {}: {}", request.getRequestURI(), ex.getMessage());
        String campo = ex.getPath().isEmpty() ? "json" : ex.getPath().get(0).getFieldName();
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError(campo, "Formato invalido para el campo: " + campo))
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja errores de validacion de Spring.
     *
     * @param ex      excepcion de argumentos de metodo invalidos
     * @param request peticion HTTP que causo el error
     * @return respuesta HTTP 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        LOG.warn("MethodArgumentNotValid en {}", request.getRequestURI());
        List<ValidationError> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ValidationError(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
        ErrorResponse errorResponse = new ErrorResponse(errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja excepciones de lista de validaciones.
     *
     * @param ex      excepcion con lista de errores
     * @param request peticion HTTP que causo el error
     * @return respuesta HTTP 400 Bad Request
     */
    @ExceptionHandler(ValidationExceptionList.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptionList(ValidationExceptionList ex, HttpServletRequest request) {
        LOG.warn("ValidationExceptionList en {} - {} errores", request.getRequestURI(), ex.getErrores().size());
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrores());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Manejo global para excepciones no controladas.
     *
     * <p>Este handler actua como fallback para cualquier excepcion
     * no manejada explicitamente. El mensaje de error interno
     * no se expone al cliente por seguridad.</p>
     *
     * @param ex      excepcion no controlada
     * @param request peticion HTTP que causo el error
     * @return respuesta HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        LOG.error("Exception no controlada en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError("general", "Error interno del servidor"))
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
