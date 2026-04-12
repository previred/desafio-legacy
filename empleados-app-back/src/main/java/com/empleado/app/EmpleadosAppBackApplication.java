package com.empleado.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class EmpleadosAppBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmpleadosAppBackApplication.class, args);
	}

}
