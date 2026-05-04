package com.mindgrid.empleados;

import com.mindgrid.empleados.adapters.outbound.database.h2.EmpleadoRepositoryImpl;
import com.mindgrid.empleados.application.service.ValidacionServiceImpl;
import com.mindgrid.empleados.application.usecase.GestionEmpleadoUseCase;
import com.mindgrid.empleados.application.usecase.GestionEmpleadoUseCaseImpl;
import com.mindgrid.empleados.domain.repository.EmpleadoRepository;
import com.mindgrid.empleados.domain.service.ValidacionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import javax.sql.DataSource;

@SpringBootApplication
@ServletComponentScan
public class EmpleadosApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmpleadosApplication.class, args);
    }

    @Bean
    public ServletContextInitializer servletContextInitializer(DataSource dataSource) {
        return servletContext -> {
            EmpleadoRepository repository      = new EmpleadoRepositoryImpl(dataSource);
            ValidacionService validacionService = new ValidacionServiceImpl();
            GestionEmpleadoUseCase useCase      = new GestionEmpleadoUseCaseImpl(repository, validacionService);
            servletContext.setAttribute("gestionEmpleadoUseCase", useCase);
        };
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> errorPageCustomizer() {
        return factory -> factory.addErrorPages(
            new ErrorPage(HttpStatus.NOT_FOUND,             "/error"),
            new ErrorPage(HttpStatus.METHOD_NOT_ALLOWED,    "/error"),
            new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error"),
            new ErrorPage(HttpStatus.BAD_REQUEST,           "/error")
        );
    }
}
