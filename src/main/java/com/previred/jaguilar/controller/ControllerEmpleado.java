package com.previred.jaguilar.controller;

import com.previred.jaguilar.model.Empleado;
import com.previred.jaguilar.model.ErrorValidacion;
import com.previred.jaguilar.model.RespuestaError;
import com.previred.jaguilar.service.EmpleadoServicio;
import com.previred.jaguilar.service.EmpleadoServicioImpl;
import com.previred.jaguilar.util.JsonUtil;
import com.previred.jaguilar.util.LogUtil;
import com.previred.jaguilar.util.RespuestaUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet("/api/empleados")
public class ControllerEmpleado extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String CODIGO_VALIDACION = "VALIDATION_ERROR";
    private static final String CODIGO_BODY_INVALIDO = "INVALID_REQUEST_BODY";
    private static final String CODIGO_ERROR_INTERNO = "INTERNAL_SERVER_ERROR";

    private final EmpleadoServicio empleadoServicio;

    public ControllerEmpleado() {
        super();
        this.empleadoServicio = new EmpleadoServicioImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Empleado> empleados = empleadoServicio.listarEmpleados();
            String json = JsonUtil.convertirAJson(empleados);

            RespuestaUtil.configurarRespuestaJson(response, HttpServletResponse.SC_OK);
            RespuestaUtil.escribirJson(response, json);

            LogUtil.info("GET /api/empleados ejecutado correctamente.");

        } catch (IOException e) {
            LogUtil.error("Error al obtener empleados.", e);
            enviarErrorInterno(response, "Error interno al obtener empleados.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String body = leerBody(request);
            Empleado empleado = JsonUtil.convertirDesdeJson(body, Empleado.class);

            List<ErrorValidacion> errores = new ArrayList<>();
            empleadoServicio.registrarEmpleado(empleado, errores);

            if (!errores.isEmpty()) {
                LogUtil.warning("POST /api/empleados con errores de validación.");
                responderValidacion(response, errores);
                return;
            }

            responderJson(response, HttpServletResponse.SC_CREATED, empleado);
            LogUtil.info("Empleado registrado correctamente con id=" + empleado.getId());

        } catch (IOException e) {
            LogUtil.error("Error al procesar POST /api/empleados.", e);

            RespuestaError respuestaError = RespuestaUtil.crearRespuestaError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    CODIGO_BODY_INVALIDO,
                    "No se pudo procesar la solicitud.",
                    Collections.singletonList(
                            new ErrorValidacion("request", "El cuerpo de la solicitud no es válido.")
                    )
            );

            responderJson(response, HttpServletResponse.SC_BAD_REQUEST, respuestaError);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ErrorValidacion> errores = new ArrayList<>();
        Long id = convertirId(request.getParameter("id"));

        empleadoServicio.eliminarEmpleado(id, errores);

        if (!errores.isEmpty()) {
            LogUtil.warning("DELETE /api/empleados con id inválido o inexistente: " + request.getParameter("id"));
            responderValidacion(response, errores);
            return;
        }

        responderJson(
                response,
                HttpServletResponse.SC_OK,
                Collections.singletonMap("mensaje", "Empleado eliminado correctamente.")
        );

        LogUtil.info("Empleado eliminado correctamente con id=" + id);
    }

    private void responderValidacion(HttpServletResponse response, List<ErrorValidacion> errores) {
        RespuestaError respuestaError = RespuestaUtil.crearRespuestaError(
                HttpServletResponse.SC_BAD_REQUEST,
                CODIGO_VALIDACION,
                "Error de validación",
                errores
        );

        responderJson(response, HttpServletResponse.SC_BAD_REQUEST, respuestaError);
    }

    private void responderJson(HttpServletResponse response, int status, Object objeto) {
        try {
            RespuestaUtil.escribirObjetoJson(response, status, objeto);
        } catch (IOException e) {
            LogUtil.error("Error al escribir respuesta JSON.", e);
            enviarErrorInterno(response, "Error interno al escribir la respuesta.");
        }
    }

    private void enviarErrorInterno(HttpServletResponse response, String mensaje) {
        try {
            RespuestaError respuestaError = RespuestaUtil.crearRespuestaError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    CODIGO_ERROR_INTERNO,
                    mensaje,
                    Collections.emptyList()
            );

            RespuestaUtil.escribirObjetoJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    respuestaError
            );
        } catch (IOException e) {
            LogUtil.error("Error al enviar respuesta de error.", e);
        }
    }

    private String leerBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                sb.append(linea);
            }
        }

        return sb.toString();
    }

    private Long convertirId(String idParam) {
        try {
            return Long.valueOf(idParam);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }
}