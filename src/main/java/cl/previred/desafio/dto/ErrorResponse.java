package cl.previred.desafio.dto;

import java.util.List;

public class ErrorResponse {
    private List<ValidationError> errores;

    public ErrorResponse() {
    }

    public ErrorResponse(List<ValidationError> errores) {
        this.errores = errores;
    }

    public List<ValidationError> getErrores() {
        return errores;
    }

    public void setErrores(List<ValidationError> errores) {
        this.errores = errores;
    }
}