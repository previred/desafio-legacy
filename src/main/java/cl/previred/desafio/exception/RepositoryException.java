package cl.previred.desafio.exception;

/**
 * Excepcion para errores de acceso a datos.
 *
 * <p>Se lanza cuando ocurre un problema al interactuar con la base
 * de datos, como:</p>
 * <ul>
 *   <li>Errores de conexion</li>
 *   <li>Violaciones de constraints (unique, foreign key, etc.)</li>
 *   <li>Timeouts de queries</li>
 *   <li>Errores de SQL syntax</li>
 * </ul>
 *
 * <p>Esta clase extiende de {@link TechnicalException} indicando que
 * es un error de nivel tecnico/infrastructura.</p>
 *
 * @see TechnicalException
 * @see GlobalExceptionHandler
 * @since 1.0
 */
public class RepositoryException extends TechnicalException {

    /**
     * Constructor con mensaje.
     *
     * @param message mensaje descriptivo del error
     */
    public RepositoryException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa raiz.
     *
     * @param message mensaje descriptivo del error
     * @param cause   excepcion JDBC que causo este error
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
