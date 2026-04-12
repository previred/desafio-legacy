package com.empleados.config;

import com.empleados.controller.EmpleadoServlet;
import com.empleados.service.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validator;

/**
 * Configuracion de registro del Servlet de empleados en el contenedor Tomcat embebido.
 * <p>
 * Registra {@link EmpleadoServlet} en la ruta {@code /api/empleados/*}
 * inyectando sus dependencias (service, validator, objectMapper) desde el contexto Spring.
 * </p>
 */
@Configuration
public class ServletConfig {

    /**
     * Registra el {@link EmpleadoServlet} como bean del contenedor Servlet.
     *
     * @param empleadoService servicio de logica de negocio
     * @param validator       validador de Bean Validation (JSR-380)
     * @param objectMapper    serializador/deserializador JSON de Jackson
     * @return bean de registro con URL pattern {@code /api/empleados/*}
     */
    @Bean
    public ServletRegistrationBean<EmpleadoServlet> empleadoServletRegistration(
            EmpleadoService empleadoService,
            Validator validator,
            ObjectMapper objectMapper) {

        EmpleadoServlet servlet = new EmpleadoServlet(empleadoService, validator, objectMapper);
        return new ServletRegistrationBean<>(servlet, "/api/empleados/*");
    }
}
