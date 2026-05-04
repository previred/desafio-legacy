package cl.previred.desafio_legacy.repocitory.configure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Db Config
 *
 * @author Christopher Gaete O.
 * @version 1.0.0, 01-05-2026
 */
public class DbConfig {
	private static final Logger logger = LoggerFactory.getLogger(DbConfig.class);
    private static final String URL = "jdbc:h2:mem:desafiodb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
        	logger.error(e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * 
     */
    public static void initTables() {
        String sql = "CREATE TABLE IF NOT EXISTS empleados (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "name VARCHAR(100), " +
                     "last_name VARCHAR(100), " +
                     "rut VARCHAR(20) UNIQUE, " +
                     "charget VARCHAR(100), " +
                     "salary DOUBLE)";
        
        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Base de datos H2 inicializada y tabla creada.");
        } catch (SQLException e) {
        	logger.error(e.getMessage());
        }
    }
}