package cl.previred.config;

import cl.previred.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Configuration
public class DatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            try (Connection conn = DatabaseUtil.getConnection(); Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS empleados (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "nombre VARCHAR(100) NOT NULL," +
                        "apellido VARCHAR(100) NOT NULL," +
                        "rut_dni VARCHAR(30) NOT NULL UNIQUE," +
                        "cargo VARCHAR(120) NOT NULL," +
                        "salario INTEGER NOT NULL)");
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM empleados")) {
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        insertar(conn, "victor", "Montes", "11.111.111-1", "Desarrollador", 150000);
                        insertar(conn, "victor1", "M1", "22.222.222-2", "Scrum Master", 1600000);
                        insertar(conn, "victor2", "M2", "33.333.333-3", "Lider", 2500000);
                        log.info("Datos demo insertados correctamente");
                    }
                }
            }
        };
    }

    private void insertar(Connection conn, String nombre, String apellido, String rutDni, String cargo, int salario) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO empleados(nombre, apellido, rut_dni, cargo, salario) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, rutDni);
            ps.setString(4, cargo);
            ps.setInt(5, salario);
            ps.executeUpdate();
        }
    }
}
