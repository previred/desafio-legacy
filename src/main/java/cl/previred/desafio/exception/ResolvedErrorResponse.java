package cl.previred.desafio.exception;

import cl.previred.desafio.dto.ErrorResponse;

/**
 * Wrapper inmutable que contiene el codigo de estado HTTP y el cuerpo
 * de respuesta de error resuelto desde una excepcion.
 *
 * <p>Esta clase es utilizada por {@link ApiExceptionResolver} para
 * encapsular la respuesta de error antes de ser enviada al cliente.</p>
 *
 * @see ApiExceptionResolver
 * @see ErrorResponse
 * @since 1.0
 */
public class ResolvedErrorResponse {

    /** Codigo de estado HTTP de la respuesta. */
    private final int status;

    /** Cuerpo de la respuesta de error. */
    private final ErrorResponse body;

    /**
     * Constructor con estado y cuerpo.
     *
     * @param status codigo de estado HTTP (200, 400, 404, 500, etc.)
     * @param body   cuerpo de la respuesta de error
     */
    public ResolvedErrorResponse(int status, ErrorResponse body) {
        this.status = status;
        this.body = body;
    }

    /**
     * Obtiene el codigo de estado HTTP.
     *
     * @return codigo de estado
     */
    public int getStatus() {
        return status;
    }

    /**
     * Obtiene el cuerpo de la respuesta de error.
     *
     * @return ErrorResponse con los detalles del error
     */
    public ErrorResponse getBody() {
        return body;
    }
}