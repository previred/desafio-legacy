package cl.previred.config;

import cl.previred.service.EmpleadoService;
import cl.previred.servlet.EmpleadoServlet;
import cl.previred.servlet.HomeServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<HomeServlet> homeServletRegistration() {
        return new ServletRegistrationBean<HomeServlet>(new HomeServlet(), "/home");
    }

    @Bean
    public ServletRegistrationBean<EmpleadoServlet> empleadoServletRegistration(EmpleadoService empleadoService) {
        return new ServletRegistrationBean<EmpleadoServlet>(new EmpleadoServlet(empleadoService), "/api/empleados/*");
    }
}
