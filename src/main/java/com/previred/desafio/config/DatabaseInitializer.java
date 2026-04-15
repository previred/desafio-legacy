package com.previred.desafio.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.springframework.stereotype.Component;

/**
 * Ejecuta los scripts iniciales de base de datos al arrancar la aplicación.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final ConnectionFactory connectionFactory;
    private final ResourceLoader resourceLoader;

    public DatabaseInitializer(ConnectionFactory connectionFactory, ResourceLoader resourceLoader) {
        this.connectionFactory = connectionFactory;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Carga el esquema y los datos iniciales en la base H2.
     *
     * @param args argumentos de arranque
     * @throws Exception si ocurre un error al ejecutar los scripts
     */
    @Override
    public void run(String... args) throws Exception {
        executeScript("classpath:schema.sql");
        executeScript("classpath:data.sql");
    }

    private void executeScript(String location) throws IOException, SQLException {
        Resource resource = resourceLoader.getResource(location);
        if (!resource.exists()) {
            LOGGER.info("No se encontro el script {}", location);
            return;
        }

        String content;
        try (InputStream inputStream = resource.getInputStream()) {
            content = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
        String[] statements = Arrays.stream(content.split(";"))
                .map(String::trim)
                .filter(statement -> !statement.isEmpty())
                .toArray(String[]::new);

        try (Connection connection = connectionFactory.getConnection()) {
            for (String sql : statements) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                }
            }
        }

        LOGGER.info("Script ejecutado correctamente: {}", location);
    }
}
