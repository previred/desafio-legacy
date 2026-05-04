package com.mindgrid.empleados.domain.exception;

import java.util.Collections;
import java.util.List;

public class BusinessException extends RuntimeException {

    private final List<String> errores;

    public BusinessException(List<String> errores) {
        super(String.join("; ", errores));
        this.errores = Collections.unmodifiableList(errores);
    }

    public BusinessException(String mensaje) {
        this(Collections.singletonList(mensaje));
    }

    public List<String> getErrores() {
        return errores;
    }
}
