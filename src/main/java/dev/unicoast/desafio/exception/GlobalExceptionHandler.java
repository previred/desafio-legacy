package dev.unicoast.desafio.exception;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String KEY_ERROR = "error";
    private static final String KEY_MENSAJE = "mensaje";

    private GlobalExceptionHandler() {}

    public static void handle(HttpServletResponse response, Exception ex) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorDetail detail = resolveError(ex);

        if (detail.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
            LOG.error("Excepción no controlada", ex);
        } else {
            LOG.warn("{}: {}", detail.errorType, ex.getMessage());
        }

        Map<String, String> errorResponse = new LinkedHashMap<>();
        errorResponse.put(KEY_ERROR, detail.errorType);
        errorResponse.put(KEY_MENSAJE, detail.message);

        response.setStatus(detail.status);
        response.getWriter().write(gson.toJson(errorResponse));
    }

    private static ErrorDetail resolveError(Exception ex) {
        if (ex instanceof BusinessValidationException) 
            return new ErrorDetail(HttpServletResponse.SC_BAD_REQUEST, "Validación de Negocio", ex.getMessage());
        
        if (ex instanceof ResourceNotFoundException) 
            return new ErrorDetail(HttpServletResponse.SC_NOT_FOUND, "Recurso No Encontrado", ex.getMessage());
        
        if (ex instanceof NumberFormatException || ex instanceof JsonSyntaxException) 
            return new ErrorDetail(HttpServletResponse.SC_BAD_REQUEST, "Formato Inválido", "Los datos enviados no son válidos o contienen tipos incorrectos");
        
        return new ErrorDetail(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error Interno del Servidor", "Ocurrió un error inesperado al procesar la solicitud.");
    }

    private static class ErrorDetail {
        final int status;
        final String errorType;
        final String message;

        ErrorDetail(int status, String errorType, String message) {
            this.status = status;
            this.errorType = errorType;
            this.message = message;
        }
    }
}
