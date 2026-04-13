package com.previred.jaguilar.model;

import java.util.List;

public class RespuestaError {

    private int status;
    private String codigo;
    private String mensaje;
    private List<ErrorValidacion> errores;

    public RespuestaError() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<ErrorValidacion> getErrores() {
        return errores;
    }

    public void setErrores(List<ErrorValidacion> errores) {
        this.errores = errores;
    }
}