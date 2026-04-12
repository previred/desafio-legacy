package cl.previred.desafio.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String campo;

    public ResourceNotFoundException(String campo, String message) {
        super(message);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}