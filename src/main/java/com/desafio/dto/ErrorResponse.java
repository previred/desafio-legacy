package com.desafio.dto;

import java.util.List;

/**
 * DTO para respuestas de error estandarizadas.
 * Contiene una lista de mensajes de error para devolverlos como JSON al cliente.
 */
public class ErrorResponse {

    private int status;
    private String mensaje;
    private List<String> errores;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String mensaje, List<String> errores) {
        this.status = status;
        this.mensaje = mensaje;
        this.errores = errores;
    }

    // --- Getters y Setters ---

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<String> getErrores() {
        return errores;
    }

    public void setErrores(List<String> errores) {
        this.errores = errores;
    }
}

