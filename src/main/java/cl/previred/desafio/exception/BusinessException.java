package cl.previred.desafio.exception;

public class BusinessException extends RuntimeException {

    private final String codigo;

    public BusinessException(String codigo, String message) {
        super(message);
        this.codigo = codigo;
    }

    public BusinessException(String codigo, String message, Throwable cause) {
        super(message, cause);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
