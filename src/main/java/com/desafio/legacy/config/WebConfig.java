package com.desafio.legacy.config;

import com.desafio.legacy.controllers.EmpleadoController;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Bean
    public ServletRegistrationBean<EmpleadoController> registrarEmpleadoServlet(EmpleadoController controller) {
        ServletRegistrationBean<EmpleadoController> bean = new ServletRegistrationBean<>(controller);
        bean.addUrlMappings("/api/empleados", "/api/empleados/*");
        bean.setLoadOnStartup(1);
        return bean;
    }
}
