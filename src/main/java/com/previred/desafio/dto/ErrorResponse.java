package com.previred.desafio.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {

    private int status;
    private String mensaje;
    private List<String> errores;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String mensaje, List<String> errores) {
        this.status = status;
        this.mensaje = mensaje;
        this.errores = errores;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String mensaje) {
        this(status, mensaje, null);
    }

    public int getStatus() { return status; }
    public String getMensaje() { return mensaje; }
    public List<String> getErrores() { return errores; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
