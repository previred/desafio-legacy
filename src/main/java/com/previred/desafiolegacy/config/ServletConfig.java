package com.previred.desafiolegacy.config;

import com.previred.desafiolegacy.servlet.EmpleadoServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<EmpleadoServlet> empleadoServlet() {
        ServletRegistrationBean<EmpleadoServlet> registration =
                new ServletRegistrationBean<>(new EmpleadoServlet(), "/api/empleados/*");
        registration.setLoadOnStartup(1);
        return registration;
    }

}
