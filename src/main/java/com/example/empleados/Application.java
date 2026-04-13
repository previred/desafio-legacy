package com.example.empleados;

import com.example.empleados.util.DBConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class Application {

    public static void main(String[] args) {
        try {
            DBConnection.getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SpringApplication.run(Application.class, args);
    }
}
