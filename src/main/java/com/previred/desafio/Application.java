package com.previred.desafio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación.
 */
@SpringBootApplication
public class Application {

    /**
     * Inicia el contexto Spring Boot.
     *
     * @param args argumentos de arranque
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
