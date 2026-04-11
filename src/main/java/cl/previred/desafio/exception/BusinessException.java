package cl.previred.desafio.exception;

/**
 * Excepcion para violations de reglas de negocio.
 *
 * <p>Se utiliza cuando una operacion infringe una regla de negocio
 * especifica del dominio, como por ejemplo:</p>
 * <ul>
 *   <li>Intentar crear un empleado duplicado</li>
 *   <li>Operacion que viola restricciones de negocio</li>
 *   <li>Estado no valido para una transaccion</li>
 * </ul>
 *
 * <p>Incluye un codigo de error para facilitar el manejo programmatico
 * del error en la capa de presentacion.</p>
 *
 * @see GlobalExceptionHandler
 * @since 1.0
 */
public class BusinessException extends RuntimeException {

    /** Codigo de error de negocio para identificacion programatica. */
    private final String codigo;

    /**
     * Constructor con codigo y mensaje.
     *
     * @param codigo  codigo de error de negocio
     * @param message mensaje descriptivo del error
     */
    public BusinessException(String codigo, String message) {
        super(message);
        this.codigo = codigo;
    }

    /**
     * Constructor con codigo, mensaje y causa raiz.
     *
     * @param codigo  codigo de error de negocio
     * @param message mensaje descriptivo del error
     * @param cause   excepcion que causo este error
     */
    public BusinessException(String codigo, String message, Throwable cause) {
        super(message, cause);
        this.codigo = codigo;
    }

    /**
     * Obtiene el codigo de error de negocio.
     *
     * @return codigo de error
     */
    public String getCodigo() {
        return codigo;
    }
}
