package com.empleado.app.http.base;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.empleado.app.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Clase base para todos los Servlets de la aplicación.
 * Centraliza el manejo de errores, logging y respuestas JSON.
 * Facilita la reutilización de código en los controladores.
 * Autor: Cristian Palacios
 */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Mapper para serializar y deserializar JSON.
     */
    protected ObjectMapper mapper = new ObjectMapper();

    /**
     * Logger para registrar eventos y errores en la aplicación.
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Ejecuta una acción controlada con manejo centralizado de excepciones.
     * Permite estandarizar la respuesta ante errores de negocio o del sistema.
     */
    protected void execute(HttpServletResponse resp, ServletAction action) throws IOException {

        try {
        	
            action.execute();

        } catch (ApiException ex) {

            logger.warn("Error de validación: {}", ex.getErrors());

            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getErrors());

        } catch (Exception ex) {

            logger.error("Error interno del servidor", ex);

            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    List.of("No pudimos procesar tu solicitud en este momento."));
        }
    }

    /**
     * Envía una respuesta HTTP en formato JSON con un estado específico.
     */
    protected void sendJson(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        mapper.writeValue(resp.getOutputStream(), data);
    }

    /**
     * Envía una respuesta de error en formato JSON estandarizado.
     */
    protected void sendError(HttpServletResponse resp, int status, Object errors) throws IOException {
        sendJson(resp, status, Map.of("errors", errors));
    }
    
}