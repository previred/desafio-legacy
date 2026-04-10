package app.v1.cl.desafiolegacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class DesafioLegacyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesafioLegacyApplication.class, args);
    }

}
