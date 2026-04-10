package com.desafio.config;

import com.desafio.controller.EmpleadoServlet;
import com.desafio.service.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Servlets.
 * Registra el EmpleadoServlet en el contenedor embebido de Spring Boot
 * mediante ServletRegistrationBean (NO usa @RestController ni Spring Web MVC).
 */
@Configuration
public class ServletConfig {

    /**
     * Bean compartido de ObjectMapper para serialización/deserialización JSON.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    /**
     * Registra el EmpleadoServlet en la ruta /api/empleados/*.
     * Esto es equivalente a usar @WebServlet, pero gestionado por Spring Boot.
     */
    @Bean
    public ServletRegistrationBean<EmpleadoServlet> empleadoServletRegistration(
            EmpleadoService empleadoService,
            ObjectMapper objectMapper) {

        EmpleadoServlet servlet = new EmpleadoServlet(empleadoService, objectMapper);

        ServletRegistrationBean<EmpleadoServlet> registration =
                new ServletRegistrationBean<>(servlet, "/api/empleados/*");

        registration.setName("empleadoServlet");
        registration.setLoadOnStartup(1);

        return registration;
    }
}

