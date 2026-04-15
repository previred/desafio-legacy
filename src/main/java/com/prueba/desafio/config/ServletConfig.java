package com.prueba.desafio.config;
import com.prueba.desafio.service.EmpleadoService;
import com.prueba.desafio.servlet.EmpleadoServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<EmpleadoServlet> empleadoServletRegistration(EmpleadoService empleadoService) {
        ServletRegistrationBean<EmpleadoServlet> registrationBean =
                new ServletRegistrationBean<EmpleadoServlet>(
                        new EmpleadoServlet(empleadoService),
                        "/api/empleados",
                        "/api/empleados/*"
                );

        registrationBean.setName("empleadoServlet");
        return registrationBean;
    }
}