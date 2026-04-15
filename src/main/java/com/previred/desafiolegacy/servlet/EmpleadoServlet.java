package com.previred.desafiolegacy.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.previred.desafiolegacy.exception.ValidacionException;
import com.previred.desafiolegacy.model.Empleado;
import com.previred.desafiolegacy.service.EmpleadoService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class EmpleadoServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoServlet.class);

    private EmpleadoService empleadoService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(config.getServletContext());
        this.empleadoService = context.getBean(EmpleadoService.class);
        this.objectMapper = new ObjectMapper();
        logger.info("EmpleadoServlet inicializado correctamente");
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        logger.info("GET /api/empleados");
        List<Empleado> empleados = empleadoService.obtenerTodos();
        enviarRespuestaJson(response, HttpServletResponse.SC_OK, empleados);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        logger.info("POST /api/empleados");
        try {
            Empleado empleado = objectMapper.readValue(request.getInputStream(), Empleado.class);
            Empleado creado = empleadoService.crear(empleado);
            enviarRespuestaJson(response, HttpServletResponse.SC_CREATED, creado);
        } catch (ValidacionException e) {
            logger.warn("Validacion fallida - campo: {}, mensaje: {}", e.getCampo(), e.getMessage());
            enviarRespuestaJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("campo", e.getCampo(), "error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error procesando POST /api/empleados: {}", e.getMessage());
            enviarRespuestaJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "El cuerpo de la solicitud no es un JSON válido"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        logger.info("DELETE /api/empleados");
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarRespuestaJson(response, HttpServletResponse.SC_BAD_REQUEST,
                        Map.of("error", "Debe indicar el ID del empleado"));
                return;
            }
            Long id = Long.parseLong(pathInfo.substring(1));
            boolean eliminado = empleadoService.eliminar(id);
            if (eliminado) {
                enviarRespuestaJson(response, HttpServletResponse.SC_OK,
                        Map.of("mensaje", "Empleado eliminado correctamente"));
            } else {
                enviarRespuestaJson(response, HttpServletResponse.SC_NOT_FOUND,
                        Map.of("error", "Empleado no encontrado con id: " + id));
            }
        } catch (NumberFormatException e) {
            logger.warn("ID invalido en path: {}", request.getPathInfo());
            enviarRespuestaJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "El ID debe ser un número válido"));
        }
    }


    private void enviarRespuestaJson(HttpServletResponse response,
                                     int status, Object body) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        objectMapper.writeValue(response.getWriter(), body);
    }
}
