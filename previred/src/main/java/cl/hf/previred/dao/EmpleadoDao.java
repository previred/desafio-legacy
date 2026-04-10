package cl.hf.previred.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cl.hf.previred.model.EmpleadoDTO;

@Repository
public class EmpleadoDao {
    
    private static final Logger LOGGER = Logger.getLogger(EmpleadoDao.class.getName());

    @Autowired
    private DataSource dataSource;

    public List<EmpleadoDTO> listar() throws SQLException {
        List<EmpleadoDTO> empleados = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, rut, cargo, salario, bono, descuento FROM empleados ORDER BY id DESC";
        
        // Try-with-resources garantiza el cierre de la conexión al finalizar el bloque
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                empleados.add(mapResultSetToEmpleado(rs));
            }
        }
        LOGGER.info("Se listaron " + empleados.size() + " empleados.");
        return empleados;
    }

    public void guardar(EmpleadoDTO emp) throws SQLException {
        String sql = "INSERT INTO empleados (nombre, apellido, rut, cargo, salario, bono, descuento) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, emp.getNombre());
            ps.setString(2, emp.getApellido());
            ps.setString(3, emp.getRut());
            ps.setString(4, emp.getCargo());
            ps.setDouble(5, emp.getSalario());
            ps.setDouble(6, emp.getBono());
            ps.setDouble(7, emp.getDescuento());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("No se pudo insertar el empleado, no hubo filas afectadas.");
            }

            // Recuperar el ID generado por H2
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    emp.setId(generatedKeys.getLong(1));
                    LOGGER.info("Empleado guardado con ID: " + emp.getId());
                }
            }
        }
    }

    public void eliminar(Long id) throws SQLException {
        String sql = "DELETE FROM empleados WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            LOGGER.info("Eliminación de ID " + id + ". Filas afectadas: " + rows);
        }
    }

    public boolean existeRut(String rut) throws SQLException {
        String sql = "SELECT 1 FROM empleados WHERE rut = ? LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rut);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Retorna true si encuentra al menos un registro
            }
        }
    }

    /**
     * Helper para mapear el ResultSet al DTO. 
     * Centralizar esto facilita el mantenimiento si la tabla cambia.
     */
    private EmpleadoDTO mapResultSetToEmpleado(ResultSet rs) throws SQLException {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setId(rs.getLong("id"));
        dto.setNombre(rs.getString("nombre"));
        dto.setApellido(rs.getString("apellido"));
        dto.setRut(rs.getString("rut"));
        dto.setCargo(rs.getString("cargo"));
        dto.setSalario(rs.getDouble("salario"));
        dto.setBono(rs.getDouble("bono"));
        dto.setDescuento(rs.getDouble("descuento"));
        return dto;
    }
}