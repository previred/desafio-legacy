package com.previred.desafio.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final String URL = "jdbc:h2:mem:previred_db;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void inicializarTablas(){
        String sql = "CREATE TABLE IF NOT EXISTS empleado (" +
                "id LONG AUTO_INCREMENT PRIMARY KEY, " +
                "nombre VARCHAR(100), " +
                "apellido VARCHAR(100), " +
                "rut VARCHAR(20) UNIQUE, " +
                "cargo VARCHAR(100), " +
                "salario DOUBLE, " +
                "bonos DOUBLE," +
                "descuentos DOUBLE" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Tabla 'empleado' inicializada correctamente en H2.");
        } catch (SQLException e) {
            logger.error("Error al inicializar la base de datos: ", e);
        }
    }

}
