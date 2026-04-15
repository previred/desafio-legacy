package com.desafio;

import com.desafio.repository.EmpleadoRepository;
import com.desafio.servlet.EmpleadoServlet;
import com.desafio.util.DatabaseUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DesafioApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesafioApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<EmpleadoServlet> servletRegistration() {
        return new ServletRegistrationBean<>(new EmpleadoServlet(), "/api/empleados");
    }

    @Bean
    public CommandLineRunner init() {
        return args -> {
            DatabaseUtil.initDB();
        };
    }

}
