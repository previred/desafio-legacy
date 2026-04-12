package cl.previred.desafio.exception;

/**
 * Excepcion para errores de recurso no encontrado.
 *
 * <p>Se lanza cuando se intenta acceder a un recurso que no existe
 * en el sistema, como por ejemplo:</p>
 * <ul>
 *   <li>Empleado con ID que no existe</li>
 *   <li>Recurso solicitado que fue eliminado</li>
 *   <li>Referencia a entidad relacionada que no existe</li>
 * </ul>
 *
 * @since 1.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /** Nombre del campo que contiene la referencia al recurso no encontrado. */
    private final String campo;

    /**
     * Constructor con campo y mensaje.
     *
     * @param campo   nombre del campo que referenciaba el recurso no encontrado
     * @param message mensaje descriptivo del error
     */
    public ResourceNotFoundException(String campo, String message) {
        super(message);
        this.campo = campo;
    }

    /**
     * Obtiene el nombre del campo que referenciaba el recurso no encontrado.
     *
     * @return nombre del campo
     */
    public String getCampo() {
        return campo;
    }
}