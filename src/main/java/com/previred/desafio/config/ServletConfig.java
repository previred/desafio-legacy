package com.previred.desafio.config;

import com.previred.desafio.servlet.EmpleadoServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<EmpleadoServlet> empleadoServletRegistration(EmpleadoServlet empleadoServlet) {
        ServletRegistrationBean<EmpleadoServlet> registration = new ServletRegistrationBean<>(empleadoServlet, "/api/empleados");
        registration.setLoadOnStartup(1);
        return registration;
    }
}
