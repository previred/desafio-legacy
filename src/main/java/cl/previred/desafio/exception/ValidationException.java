package cl.previred.desafio.exception;

/**
 * Excepcion para errores de validacion individuales.
 *
 * <p>Se utiliza cuando una unica validacion falla, por ejemplo:</p>
 * <ul>
 *   <li>Un campo requerido esta vacio</li>
 *   <li>Un formato es invalido</li>
 *   <li>Una regla de negocio no se cumple</li>
 * </ul>
 *
 * <p>Esta excepcion incluye el nombre del campo que fallo para facilitar
 * el feedback al usuario.</p>
 *
 * @see ValidationExceptionList
 * @see GlobalExceptionHandler
 * @since 1.0
 */
public class ValidationException extends RuntimeException {

    /** Nombre del campo que causo el error de validacion. */
    private final String campo;

    /**
     * Constructor con campo y mensaje.
     *
     * @param campo   nombre del campo que fallo la validacion
     * @param message mensaje descriptivo del error
     */
    public ValidationException(String campo, String message) {
        super(message);
        this.campo = campo;
    }

    /**
     * Obtiene el nombre del campo que fallo.
     *
     * @return nombre del campo
     */
    public String getCampo() {
        return campo;
    }
}
