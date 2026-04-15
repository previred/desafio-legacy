package com.previred.desafio.config;

import com.previred.desafio.servlet.EmpleadoServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra los servlets expuestos por la aplicación.
 */
@Configuration
public class ServletConfig {

    /**
     * Asocia el servlet de empleados a su endpoint HTTP.
     *
     * @param empleadoServlet servlet principal de empleados
     * @return registro del servlet en /api/empleados
     */
    @Bean
    public ServletRegistrationBean<EmpleadoServlet> empleadoServletRegistration(EmpleadoServlet empleadoServlet) {
        ServletRegistrationBean<EmpleadoServlet> registration = new ServletRegistrationBean<>(empleadoServlet, "/api/empleados");
        registration.setLoadOnStartup(1);
        return registration;
    }
}
