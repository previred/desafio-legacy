package com.previred.jaguilar.util;

import com.previred.jaguilar.model.ErrorValidacion;
import com.previred.jaguilar.model.RespuestaError;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public final class RespuestaUtil {

    private RespuestaUtil() {
    }

    public static void configurarRespuestaJson(HttpServletResponse response, int status) {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
    }

    public static void escribirJson(HttpServletResponse response, String json) throws IOException {
        response.getWriter().write(json);
        response.getWriter().flush();
    }

    public static void escribirObjetoJson(HttpServletResponse response, int status, Object objeto) throws IOException {
        configurarRespuestaJson(response, status);
        escribirJson(response, JsonUtil.convertirAJson(objeto));
    }

    public static RespuestaError crearRespuestaError(int status, String codigo, String mensaje, List<ErrorValidacion> errores) {
        RespuestaError respuestaError = new RespuestaError();
        respuestaError.setStatus(status);
        respuestaError.setCodigo(codigo);
        respuestaError.setMensaje(mensaje);
        respuestaError.setErrores(errores);
        return respuestaError;
    }
}