    package com.previred.desafiolegacy.infrastructure.config;

    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;
    import java.sql.Statement;

    public class DatabaseConfig {
        private static DatabaseConfig instancia;
        private final String url = "jdbc:h2:mem:empleados_db;DB_CLOSE_DELAY=-1";
        private final String user = "sa";
        private final String password = "";

        private DatabaseConfig() {
            initSQL();
        }

        public static synchronized DatabaseConfig getInstancia() {
            if (instancia == null) {
                instancia = new DatabaseConfig();
            }
            return instancia;
        }

        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url, user, password);
        }

        private void initSQL() {
            String sql = "CREATE TABLE IF NOT EXISTS empleado (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nombre VARCHAR(100), " +
                    "apellido VARCHAR(100), " +
                    "rut_dni VARCHAR(20) UNIQUE, " +
                    "cargo VARCHAR(100), " +
                    "salario_base DOUBLE, " +
                    "bonos DOUBLE, " +
                    "descuentos DOUBLE)";

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Base de datos H2 inicializada.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
