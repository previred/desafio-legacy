package cl.previred.desafio.dto;

import java.util.Collections;
import java.util.List;

/**
 * Fabrica de objetos {@link ErrorResponse} para diferentes escenarios de error.
 *
 * <p>Esta clase utilitaria proporciona metodos estaticos para crear respuestas
 * de error estandarizadas en toda la aplicacion. Sigue el patron Factory
 * para garantizar consistencia en los mensajes de error.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * ErrorResponse error = ErrorResponseFactory.validationError("nombre", "Requerido");
 * ErrorResponse notFound = ErrorResponseFactory.empleadoNotFoundError(123L);
 * }</pre>
 *
 * @see ErrorResponse
 * @see ValidationError
 * @since 1.0
 */
public final class ErrorResponseFactory {

    /**
     * Constructor privado para prevenir instanciacion.
     * Pattern Utility Class.
     */
    private ErrorResponseFactory() {
    }

    /**
     * Crea un ErrorResponse con un unico error de validacion.
     *
     * @param campo   nombre del campo que tuvo el error
     * @param mensaje mensaje descriptivo del error
     * @return ErrorResponse con un solo ValidationError
     */
    public static ErrorResponse validationError(String campo, String mensaje) {
        return new ErrorResponse(Collections.singletonList(new ValidationError(campo, mensaje)));
    }

    /**
     * Crea un ErrorResponse con una lista de errores de validacion.
     *
     * @param errores lista de errores de validacion
     * @return ErrorResponse con la lista de errores
     */
    public static ErrorResponse validationError(List<ValidationError> errores) {
        return new ErrorResponse(errores);
    }

    /**
     * Crea un ErrorResponse para errores de solicitud invalida (400).
     *
     * @param campo   nombre del campo que tuvo el error
     * @param mensaje mensaje descriptivo del error
     * @return ErrorResponse para bad request
     */
    public static ErrorResponse badRequest(String campo, String mensaje) {
        return validationError(campo, mensaje);
    }

    /**
     * Crea un ErrorResponse para errores de recurso no encontrado (404).
     *
     * @param campo   nombre del campo que tuvo el error
     * @param mensaje mensaje descriptivo del error
     * @return ErrorResponse para recurso no encontrado
     */
    public static ErrorResponse notFound(String campo, String mensaje) {
        return validationError(campo, mensaje);
    }

    /**
     * Crea un ErrorResponse para errores internos del servidor (500).
     *
     * @param mensaje mensaje descriptivo del error interno
     * @return ErrorResponse con error de servidor
     */
    public static ErrorResponse internalError(String mensaje) {
        return new ErrorResponse(Collections.singletonList(new ValidationError("internal", mensaje)));
    }

    /**
     * Crea un ErrorResponse para errores de JSON invalido o no parseable.
     *
     * @return ErrorResponse para errores de parseo JSON
     */
    public static ErrorResponse jsonInvalidError() {
        return validationError("json", "JSON invalido o no pudo ser parseado");
    }

    /**
     * Crea un ErrorResponse para errores cuando el ID es requerido pero no se proporciona.
     *
     * @return ErrorResponse para ID requerido
     */
    public static ErrorResponse idRequiredError() {
        return validationError("id", "ID es requerido");
    }

    /**
     * Crea un ErrorResponse para errores cuando el ID tiene formato invalido.
     *
     * @param idParam valor del parametro ID que fue invalido
     * @return ErrorResponse para ID invalido
     */
    public static ErrorResponse idInvalidError(String idParam) {
        return validationError("id", "ID invalido: " + idParam);
    }

    /**
     * Crea un ErrorResponse para errores cuando un empleado no es encontrado.
     *
     * @param id identificador del empleado no encontrado
     * @return ErrorResponse para empleado no encontrado
     */
    public static ErrorResponse empleadoNotFoundError(Long id) {
        return validationError("id", "Empleado no encontrado con id: " + id);
    }
}