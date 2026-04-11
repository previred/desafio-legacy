package cl.previred.desafio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class PreviredDesafioLegacyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PreviredDesafioLegacyApplication.class, args);
    }
}