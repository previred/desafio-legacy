package cl.previred.desafio.exception;

public class RepositoryException extends TechnicalException {

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
