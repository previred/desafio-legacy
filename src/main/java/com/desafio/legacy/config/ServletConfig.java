package com.desafio.legacy.config;

import javax.validation.Validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.desafio.legacy.api.EmpleadoServlet;
import com.desafio.legacy.service.EmpleadoService;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<EmpleadoServlet> empleadoServletRegistration(
        EmpleadoService empleadoService,
        ObjectMapper objectMapper,
        Validator validator
    ) {
        EmpleadoServlet empleadoServlet = new EmpleadoServlet(empleadoService, objectMapper, validator);
        return new ServletRegistrationBean<EmpleadoServlet>(empleadoServlet, "/api/empleados", "/api/empleados/*");
    }
}
