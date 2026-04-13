package com.desafio.legacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(scanBasePackages = "com.desafio")
@ServletComponentScan(basePackages = "com.desafio")
public class DesafioLegacyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesafioLegacyApplication.class, args);
    }
}
