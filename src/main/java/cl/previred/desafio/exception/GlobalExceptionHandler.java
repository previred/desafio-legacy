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

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex, HttpServletRequest request) {
        LOG.warn("ValidationException en {} - Campo: {}, Mensaje: {}", 
            request.getRequestURI(), ex.getCampo(), ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError(ex.getCampo(), ex.getMessage()))
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        LOG.warn("BusinessException en {} - Codigo: {}, Mensaje: {}", 
            request.getRequestURI(), ex.getCodigo(), ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError(ex.getCodigo(), ex.getMessage()))
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ErrorResponse> handleTechnicalException(TechnicalException ex, HttpServletRequest request) {
        LOG.error("TechnicalException en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError("internal", "Error interno del servidor"))
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<ErrorResponse> handleRepositoryException(RepositoryException ex, HttpServletRequest request) {
        LOG.error("RepositoryException en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError("database", "Error interno de base de datos"))
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        LOG.warn("TypeMismatch en {} - Parametro: {}, Valor: {}", 
            request.getRequestURI(), ex.getName(), ex.getValue());
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError(ex.getName(), "Valor invalido para el parametro: " + ex.getName()))
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFormat(InvalidFormatException ex, HttpServletRequest request) {
        LOG.warn("InvalidFormat en {}: {}", request.getRequestURI(), ex.getMessage());
        String campo = ex.getPath().isEmpty() ? "json" : ex.getPath().get(0).getFieldName();
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError(campo, "Formato invalido para el campo: " + campo))
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        LOG.warn("MethodArgumentNotValid en {}", request.getRequestURI());
        List<ValidationError> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ValidationError(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
        ErrorResponse errorResponse = new ErrorResponse(errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ValidationExceptionList.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptionList(ValidationExceptionList ex, HttpServletRequest request) {
        LOG.warn("ValidationExceptionList en {} - {} errores", request.getRequestURI(), ex.getErrores().size());
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrores());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        LOG.error("Exception no controlada en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                Collections.singletonList(new ValidationError("general", "Error interno del servidor"))
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
