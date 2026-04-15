package com.previred.desafio.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.previred.desafio.dto.ApiError;
import com.previred.desafio.dto.EmpleadoRequest;
import com.previred.desafio.dto.ErrorResponse;
import com.previred.desafio.exception.BusinessException;
import com.previred.desafio.model.Empleado;
import com.previred.desafio.service.EmpleadoService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpleadoServlet.class);

    private final EmpleadoService empleadoService;
    private final ObjectMapper objectMapper;

    public EmpleadoServlet(EmpleadoService empleadoService, ObjectMapper objectMapper) {
        this.empleadoService = empleadoService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Empleado> empleados = empleadoService.findAll();
            writeJson(response, HttpServletResponse.SC_OK, empleados);
        } catch (Exception exception) {
            handleUnexpectedError(response, exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            EmpleadoRequest empleadoRequest = objectMapper.readValue(request.getInputStream(), EmpleadoRequest.class);
            Empleado empleado = empleadoService.create(empleadoRequest);
            writeJson(response, HttpServletResponse.SC_CREATED, empleado);
        } catch (BusinessException exception) {
            writeError(response, exception.getStatus(), exception.getMessage(), exception.getErrors());
        } catch (IOException exception) {
            LOGGER.warn("Error al interpretar el cuerpo de la solicitud", exception);
            writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Validation failed",
                    Collections.singletonList(ApiError.of("requestBody", "INVALID_JSON", "El cuerpo JSON es invalido"))
            );
        } catch (Exception exception) {
            handleUnexpectedError(response, exception);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idParam = request.getParameter("id");
            Long id = idParam == null ? null : Long.valueOf(idParam);
            empleadoService.deleteById(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException exception) {
            writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Validation failed",
                    Collections.singletonList(ApiError.of("id", "INVALID_ID", "El id debe ser numerico"))
            );
        } catch (BusinessException exception) {
            writeError(response, exception.getStatus(), exception.getMessage(), exception.getErrors());
        } catch (Exception exception) {
            handleUnexpectedError(response, exception);
        }
    }

    private void handleUnexpectedError(HttpServletResponse response, Exception exception) throws IOException {
        LOGGER.error("Error inesperado al procesar la solicitud de empleados", exception);
        writeError(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                Collections.singletonList(ApiError.of("UNEXPECTED_ERROR", "Ocurrio un error inesperado"))
        );
    }

    private void writeError(HttpServletResponse response, int status, String message, List<ApiError> errors)
            throws IOException {
        writeJson(response, status, new ErrorResponse(status, message, errors));
    }

    private void writeJson(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
