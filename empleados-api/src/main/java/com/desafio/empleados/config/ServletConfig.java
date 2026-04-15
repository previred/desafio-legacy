package com.desafio.empleados.config;

import com.desafio.empleados.servlet.EmpleadoServlet;
import com.desafio.empleados.service.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class ServletConfig {

    private static final Logger log = LoggerFactory.getLogger(ServletConfig.class);

    @Bean
    public ServletRegistrationBean<EmpleadoServlet> empleadoServlet(EmpleadoService empleadoService, ObjectMapper objectMapper) {
        EmpleadoServlet servlet = new EmpleadoServlet(empleadoService, objectMapper);
        ServletRegistrationBean<EmpleadoServlet> reg = new ServletRegistrationBean<>(servlet,
                "/api/empleados", "/api/empleados/*");
        reg.setName("empleadoServlet");
        reg.setLoadOnStartup(1);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        log.info("API empleados: HttpServlet registrado en /api/empleados (GET lista, POST alta, DELETE con id en path)");
        return reg;
    }
}
