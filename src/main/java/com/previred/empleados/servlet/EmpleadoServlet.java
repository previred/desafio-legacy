package com.previred.empleados.servlet;

import com.previred.empleados.dto.EmpleadoDTO;
import com.previred.empleados.dto.EmpleadoRequestDTO;
import com.previred.empleados.dto.ErrorResponseDTO;
import com.previred.empleados.exception.DuplicateRutException;
import com.previred.empleados.exception.ValidationException;
import com.previred.empleados.service.EmpleadoService;
import com.previred.empleados.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/api/empleados/*")
public class EmpleadoServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoServlet.class);

    private EmpleadoService empleadoService;

    @Override
    public void init() throws ServletException {
        super.init();
        empleadoService = WebApplicationContextUtils
                .getRequiredWebApplicationContext(getServletContext())
                .getBean(EmpleadoService.class);
        logger.info("EmpleadoServlet inicializado");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("GET /api/empleados");

        try {
            List<EmpleadoDTO> empleados = empleadoService.getAllEmpleados();
            sendJsonResponse(response, HttpServletResponse.SC_OK, empleados);
        } catch (Exception e) {
            logger.error("Error al obtener empleados", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ErrorResponseDTO("Error interno del servidor"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("POST /api/empleados");

        try {
            String json = readRequestBody(request);
            EmpleadoRequestDTO empleadoRequest = JsonUtil.fromJson(json, EmpleadoRequestDTO.class);

            EmpleadoDTO empleado = empleadoService.createEmpleado(empleadoRequest);
            sendJsonResponse(response, HttpServletResponse.SC_CREATED, empleado);

        } catch (ValidationException e) {
            logger.warn("Validación fallida: {}", e.getErrors());
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ErrorResponseDTO(e.getErrors()));

        } catch (DuplicateRutException e) {
            logger.warn("RUT duplicado: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ErrorResponseDTO(e.getMessage()));

        } catch (Exception e) {
            logger.error("Error al crear empleado", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ErrorResponseDTO("Error interno del servidor"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        logger.debug("DELETE /api/empleados{}", pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        new ErrorResponseDTO("ID de empleado no proporcionado"));
                return;
            }

            String idStr = pathInfo.substring(1);
            Long id = Long.parseLong(idStr);

            empleadoService.deleteEmpleado(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (NumberFormatException e) {
            logger.warn("ID inválido: {}", pathInfo);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    new ErrorResponseDTO("ID de empleado inválido"));

        } catch (Exception e) {
            logger.error("Error al eliminar empleado", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ErrorResponseDTO("Error interno del servidor"));
        }
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;

        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        return buffer.toString();
    }

    private void sendJsonResponse(HttpServletResponse response, int status, Object data)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(JsonUtil.toJson(data));
        out.flush();
    }

    private void sendErrorResponse(HttpServletResponse response, int status, ErrorResponseDTO error)
            throws IOException {
        sendJsonResponse(response, status, error);
    }
}
