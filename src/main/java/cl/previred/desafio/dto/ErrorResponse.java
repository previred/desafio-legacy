package cl.previred.desafio.dto;

import java.util.List;

/**
 * Objeto de Transferencia de Datos (DTO) para respuestas de error.
 *
 * <p>Esta clase envuelve una lista de errores de validacion ({@link ValidationError})
 * para retornarlos de manera consistente en todas las respuestas de error
 * de la API.</p>
 *
 * <p>Ejemplo de respuesta JSON:</p>
 * <pre>{@code
 * {
     *   "errores": [
     *     {"campo": "nombre", "mensaje": "El nombre es requerido"},
     *     {"campo": "salario", "mensaje": "Debe ser mayor a 400000"}
     *   ]
     * }
     * }</pre>
 *
 * @see ValidationError
 * @since 1.0
 */
public class ErrorResponse {

    /** Lista de errores contenidos en la respuesta. */
    private List<ValidationError> errores;

    /**
     * Constructor por defecto.
     * Requerido para deserializacion JSON.
     */
    public ErrorResponse() {
    }

    /**
     * Constructor con lista de errores.
     *
     * @param errores lista de errores de validacion
     */
    public ErrorResponse(List<ValidationError> errores) {
        this.errores = errores;
    }

    /**
     * Obtiene la lista de errores.
     *
     * @return lista de errores, puede ser null si se usa el constructor por defecto
     */
    public List<ValidationError> getErrores() {
        return errores;
    }

    /**
     * Establece la lista de errores.
     *
     * @param errores lista de errores de validacion
     */
    public void setErrores(List<ValidationError> errores) {
        this.errores = errores;
    }
}
