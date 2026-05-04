package cl.previred.util;

import cl.previred.dto.ApiResponse;
import cl.previred.exception.AppException;
import cl.previred.exception.ResourceNotFoundException;
import cl.previred.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AppExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AppExceptionHandler.class);

    private AppExceptionHandler() {
    }

    public static void handle(Exception ex, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        if (ex instanceof ValidationException) {
            ValidationException vex = (ValidationException) ex;
            log.warn("Validación: {}", vex.getErrors());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtils.writeJson(response.getOutputStream(), ApiResponse.error(vex.getMessage(), vex.getErrors()));
            return;
        }
        if (ex instanceof ResourceNotFoundException) {
            log.warn("No encontrado: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonUtils.writeJson(response.getOutputStream(), ApiResponse.error(ex.getMessage(), null));
            return;
        }
        if (ex instanceof AppException) {
            log.warn("Error de negocio: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            JsonUtils.writeJson(response.getOutputStream(), ApiResponse.error(ex.getMessage(), null));
            return;
        }
        log.error("Error interno", ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        JsonUtils.writeJson(response.getOutputStream(), ApiResponse.error("Error interno del servidor", null));
    }
}
