package cl.previred.desafio.dto;

import java.util.Collections;
import java.util.List;

public final class ErrorResponseFactory {

    private ErrorResponseFactory() {
    }

    public static ErrorResponse validationError(String campo, String mensaje) {
        return new ErrorResponse(Collections.singletonList(new ValidationError(campo, mensaje)));
    }

    public static ErrorResponse validationError(List<ValidationError> errores) {
        return new ErrorResponse(errores);
    }

    public static ErrorResponse badRequest(String campo, String mensaje) {
        return validationError(campo, mensaje);
    }

    public static ErrorResponse notFound(String campo, String mensaje) {
        return validationError(campo, mensaje);
    }

    public static ErrorResponse internalError(String mensaje) {
        return new ErrorResponse(Collections.singletonList(new ValidationError("internal", mensaje)));
    }

    public static ErrorResponse jsonInvalidError() {
        return validationError("json", "JSON invalido o no pudo ser parseado");
    }

    public static ErrorResponse idRequiredError() {
        return validationError("id", "ID es requerido");
    }

    public static ErrorResponse idInvalidError(String idParam) {
        return validationError("id", "ID invalido: " + idParam);
    }

    public static ErrorResponse empleadoNotFoundError(Long id) {
        return validationError("id", "Empleado no encontrado con id: " + id);
    }
}