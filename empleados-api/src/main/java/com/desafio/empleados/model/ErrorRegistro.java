package com.desafio.empleados.model;

import java.util.Objects;

public class ErrorRegistro {

    private String campo;
    private String mensaje;

    public ErrorRegistro() {
    }

    public ErrorRegistro(String campo, String mensaje) {
        this.campo = campo;
        this.mensaje = mensaje;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorRegistro that = (ErrorRegistro) o;
        return Objects.equals(campo, that.campo) && Objects.equals(mensaje, that.mensaje);
    }

    @Override
    public int hashCode() {
        return Objects.hash(campo, mensaje);
    }
}
