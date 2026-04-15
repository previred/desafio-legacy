package com.previred.desafio;

import com.previred.desafio.config.DatabaseConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

@ServletComponentScan
@SpringBootApplication
public class DesafioLegacyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesafioLegacyApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            DatabaseConfig.inicializarTablas();
        };
    }
}
