package com.empleados;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion Spring Boot.
 * Inicia el contenedor Tomcat embebido y carga el contexto de la aplicacion.
 */
@SpringBootApplication
public class EmpleadosApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmpleadosApplication.class, args);
    }
}
