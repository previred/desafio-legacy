package com.previred.empleados.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ErrorResponse {

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("errores")
    private List<String> errores;

    public ErrorResponse(String mensaje, List<String> errores) {
        this.mensaje = mensaje;
        this.errores = errores;
    }

    public String getMensaje() {
        return mensaje;
    }

    public List<String> getErrores() {
        return errores;
    }
}
