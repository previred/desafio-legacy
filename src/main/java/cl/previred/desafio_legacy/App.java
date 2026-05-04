package cl.previred.desafio_legacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import cl.previred.desafio_legacy.repocitory.configure.DbConfig;


@SpringBootApplication
@ServletComponentScan
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        DbConfig.initTables();
    }
    
    
}
