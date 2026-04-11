package cl.previred.desafio.exception;

public class ValidationException extends RuntimeException {

    private final String campo;

    public ValidationException(String campo, String message) {
        super(message);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
