package cl.previred.desafio.exception;

/**
 * Excepcion base para errores tecnicos no esperados.
 *
 * <p>Esta clase sirve como base para excepciones tecnicas que indican
 * problemas de infraestructura, sistemas o errores no esperados como:</p>
 * <ul>
 *   <li>Errores de conexion a base de datos</li>
 *   <li>Errores de configuracion</li>
 *   <li>Errores de red</li>
 *   <li>Violaciones de contraintes del sistema</li>
 * </ul>
 *
 * <p>En produccion, los detalles de esta excepcion no deben exponerse
 * al cliente para evitar信息披露 de informacion sensible.</p>
 *
 * @see RepositoryException
 * @see GlobalExceptionHandler
 * @since 1.0
 */
public class TechnicalException extends RuntimeException {

    /**
     * Constructor con mensaje.
     *
     * @param message mensaje descriptivo del error
     */
    public TechnicalException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa raiz.
     *
     * @param message mensaje descriptivo del error
     * @param cause   excepcion que causo este error
     */
    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
