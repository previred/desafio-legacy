package com.previred.jaguilar.util;

import com.previred.jaguilar.exception.ConexionException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConexionUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                LogUtil.error("CONEXION: No se encontró application.properties", null);
                throw new ConexionException("No se encontró application.properties");
            }

            PROPERTIES.load(input);

            String driver = PROPERTIES.getProperty("spring.datasource.driver-class-name");
            Class.forName(driver);

            LogUtil.info("CONEXION: Configuración de base de datos cargada correctamente.");

        } catch (IOException | ClassNotFoundException e) {
            LogUtil.error("CONEXION: Error al cargar configuración de base de datos", e);
            throw new ConexionException("Error al cargar la configuración de base de datos", e);
        }
    }

    private ConexionUtil() {
    }

    public static Connection obtenerConexion() {
        try {
            String url = PROPERTIES.getProperty("spring.datasource.url");
            String username = PROPERTIES.getProperty("spring.datasource.username");
            String password = PROPERTIES.getProperty("spring.datasource.password");

            return DriverManager.getConnection(url, username, password);

        } catch (SQLException e) {
            LogUtil.error("CONEXION: Error al obtener conexión a la base de datos", e);
            throw new ConexionException("Error al obtener conexión a la base de datos", e);
        }
    }
}