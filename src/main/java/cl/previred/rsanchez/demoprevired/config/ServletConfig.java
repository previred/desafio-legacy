package cl.previred.rsanchez.demoprevired.config;

import cl.previred.rsanchez.demoprevired.servlet.EmployeeServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<EmployeeServlet> empleadoServlet() {
        ServletRegistrationBean<EmployeeServlet> registration =
                new ServletRegistrationBean<>(new EmployeeServlet(), "/api/empleados/*");
        registration.setLoadOnStartup(1);
        return registration;
    }
}
