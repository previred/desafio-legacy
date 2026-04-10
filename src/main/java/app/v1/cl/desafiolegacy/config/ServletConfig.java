package app.v1.cl.desafiolegacy.config;

import app.v1.cl.desafiolegacy.repository.EmpleadoRepository;
import app.v1.cl.desafiolegacy.service.EmpleadoValidationService;
import app.v1.cl.desafiolegacy.servlet.EmpleadoServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<EmpleadoServlet> empleadoServletRegistration(
            EmpleadoRepository empleadoRepository, EmpleadoValidationService validationService) {
        EmpleadoServlet servlet = new EmpleadoServlet(empleadoRepository, validationService);
        return new ServletRegistrationBean<>(servlet, "/api/empleados");
    }
}
