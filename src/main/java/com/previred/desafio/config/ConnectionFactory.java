package com.previred.desafio.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Centraliza la creación de conexiones JDBC.
 */
@Component
public class ConnectionFactory {

    private final String url;
    private final String username;
    private final String password;

    public ConnectionFactory(
            @Value("${app.datasource.url}") String url,
            @Value("${app.datasource.username}") String username,
            @Value("${app.datasource.password}") String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Obtiene una conexión nueva hacia la base configurada.
     *
     * @return conexión JDBC activa
     * @throws SQLException si la conexión no puede abrirse
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
