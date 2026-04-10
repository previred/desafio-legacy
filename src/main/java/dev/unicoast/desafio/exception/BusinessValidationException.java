package dev.unicoast.desafio.exception;

public class BusinessValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BusinessValidationException(String message) {
        super(message);
    }
}
