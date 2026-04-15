package com.desafio.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:h2:mem:testdb", "sa", ""
        );
    }

    public static void initDB() {
        System.out.println("CREANDO TABLA...");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE empleados (" +
                    "id IDENTITY PRIMARY KEY," +
                    "nombre VARCHAR(100)," +
                    "apellido VARCHAR(100)," +
                    "dni VARCHAR(50)," +
                    "cargo VARCHAR(100)," +
                    "bono DOUBLE," +
                    "descuentos DOUBLE," +
                    "salario DOUBLE)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}