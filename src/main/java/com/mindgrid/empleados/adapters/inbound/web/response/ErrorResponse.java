package com.mindgrid.empleados.adapters.inbound.web.response;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {

    private final List<String> errores;
    private final String timestamp;

    public ErrorResponse(List<String> errores) {
        this.errores = errores;
        this.timestamp = Instant.now().toString();
    }

    public List<String> getErrores() { return errores; }
    public String getTimestamp() { return timestamp; }
}
