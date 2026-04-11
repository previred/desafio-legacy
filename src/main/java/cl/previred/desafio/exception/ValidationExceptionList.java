package cl.previred.desafio.exception;

import cl.previred.desafio.dto.ValidationError;
import java.util.List;

public class ValidationExceptionList extends RuntimeException {

    private final List<ValidationError> errores;

    public ValidationExceptionList(List<ValidationError> errores) {
        super("Validacion fallida con " + errores.size() + " errores");
        this.errores = errores;
    }

    public List<ValidationError> getErrores() {
        return errores;
    }
}
