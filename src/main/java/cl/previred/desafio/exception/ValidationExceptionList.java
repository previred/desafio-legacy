package cl.previred.desafio.exception;

import cl.previred.desafio.dto.ValidationError;
import java.util.List;

/**
 * Excepcion para agrupar multiples errores de validacion.
 *
 * <p>Se utiliza cuando varias validaciones fallan simultaneamente,
 * permitiendo retornar todos los errores en una sola respuesta.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * List<ValidationError> errores = new ArrayList<>();
 * errores.add(new ValidationError("nombre", "Requerido"));
 * errores.add(new ValidationError("salario", "Debe ser mayor a 400000"));
 * throw new ValidationExceptionList(errores);
 * }</pre>
 *
 * @see ValidationError
 * @see ValidationException
 * @since 1.0
 */
public class ValidationExceptionList extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** Lista de errores de validacion. */
    private final transient List<ValidationError> errores;

    /**
     * Constructor con lista de errores.
     *
     * @param errores lista de errores de validacion
     */
    public ValidationExceptionList(List<ValidationError> errores) {
        super("Validacion fallida con " + errores.size() + " errores");
        this.errores = errores;
    }

    /**
     * Obtiene la lista de errores de validacion.
     *
     * @return lista de errores, nunca null
     */
    public List<ValidationError> getErrores() {
        return errores;
    }
}
