package com.empleados.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Configuracion de inicializacion de la base de datos H2 en memoria.
 * <p>
 * Crea la tabla {@code empleados} al arrancar la aplicacion
 * mediante un {@link CommandLineRunner} que ejecuta DDL via JDBC.
 * </p>
 */
@Configuration
@Slf4j
public class DatabaseConfig {

    /** Sentencia DDL para crear la tabla de empleados si no existe. */
    private static final String CREATE_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS empleados ("
        + "  id BIGINT AUTO_INCREMENT PRIMARY KEY,"
        + "  nombre VARCHAR(100) NOT NULL,"
        + "  apellido VARCHAR(100) NOT NULL,"
        + "  rut VARCHAR(12) NOT NULL UNIQUE,"
        + "  cargo VARCHAR(100) NOT NULL,"
        + "  salario BIGINT NOT NULL,"
        + "  bono BIGINT NOT NULL DEFAULT 0,"
        + "  descuentos BIGINT NOT NULL DEFAULT 0"
        + ")";

    /**
     * Inicializa el esquema de la base de datos al arranque.
     *
     * @param jdbcTemplate plantilla JDBC inyectada por Spring
     * @return runner que ejecuta la creacion de tabla
     */
    @Bean
    public CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute(CREATE_TABLE_SQL);
            log.info("Tabla 'empleados' creada via JDBC en H2 en memoria");
        };
    }
}
