package cl.hf.previred.config;

import cl.hf.previred.servlet.EmpleadoServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {
    @Bean
    public ServletRegistrationBean<EmpleadoServlet> empleadoServletRegistration(EmpleadoServlet servlet) {
        return new ServletRegistrationBean<>(servlet, "/api/empleados/*");
    }
}