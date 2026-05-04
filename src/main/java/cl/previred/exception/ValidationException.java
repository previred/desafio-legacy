package cl.previred.exception;

import java.util.List;

public class ValidationException extends AppException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final List<String> errors;

    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
