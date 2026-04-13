package com.example.empleados.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final String URL =
            "jdbc:h2:mem:empleadosdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Crear tabla empleados
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS empleados (" +
                            "id IDENTITY PRIMARY KEY, " +
                            "nombre VARCHAR(50) NOT NULL, " +
                            "apellido VARCHAR(50) NOT NULL, " +
                            "rut VARCHAR(20) NOT NULL UNIQUE, " +
                            "cargo VARCHAR(50), " +
                            "salario DOUBLE NOT NULL" +
                            ")"
            );

        } catch (SQLException e) {
            throw new RuntimeException("Error inicializando BD", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
