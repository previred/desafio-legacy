package cl.previred.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.previred.repository.EmpleadoRepository;
import cl.previred.repository.impl.EmpleadoJdbcRepository;

@Configuration
public class AppConfig {

    @Bean
    public EmpleadoRepository empleadoRepository() {
        return new EmpleadoJdbcRepository();
    }

}
