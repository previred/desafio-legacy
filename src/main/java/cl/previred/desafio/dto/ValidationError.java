package cl.previred.desafio.dto;

/**
 * Objeto de Transferencia de Datos (DTO) para representar un error de validacion.
 *
 * <p>Cada instancia representa un error individual asociado a un campo
 * especifico del request. Se usa en conjunto con {@link ErrorResponse}
 * para retornar multiples errores de validacion.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * ValidationError error = new ValidationError("nombre", "El nombre es requerido");
 * }</pre>
 *
 * @see ErrorResponse
 * @since 1.0
 */
public class ValidationError {

    /** Nombre del campo que tuvo el error. */
    private String campo;

    /** Mensaje descriptivo del error. */
    private String mensaje;

    /**
     * Constructor por defecto.
     * Requerido para deserializacion JSON.
     */
    public ValidationError() {
    }

    /**
     * Constructor con campo y mensaje.
     *
     * @param campo   nombre del campo que tuvo el error
     * @param mensaje mensaje descriptivo del error
     */
    public ValidationError(String campo, String mensaje) {
        this.campo = campo;
        this.mensaje = mensaje;
    }

    /**
     * Obtiene el nombre del campo.
     *
     * @return nombre del campo
     */
    public String getCampo() {
        return campo;
    }

    /**
     * Establece el nombre del campo.
     *
     * @param campo nombre del campo
     */
    public void setCampo(String campo) {
        this.campo = campo;
    }

    /**
     * Obtiene el mensaje de error.
     *
     * @return mensaje descriptivo
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Establece el mensaje de error.
     *
     * @param mensaje mensaje descriptivo del error
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
