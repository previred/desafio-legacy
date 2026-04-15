package com.desafio.empleados.model;

import java.util.ArrayList;
import java.util.List;

public class RespuestaErrorValidacion {

    private List<ErrorRegistro> errores = new ArrayList<>();

    public List<ErrorRegistro> getErrores() {
        return errores;
    }

    public void setErrores(List<ErrorRegistro> errores) {
        this.errores = errores;
    }
}
