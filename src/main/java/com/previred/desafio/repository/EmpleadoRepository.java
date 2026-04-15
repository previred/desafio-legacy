package com.previred.desafio.repository;

import com.previred.desafio.config.DatabaseConfig;
import com.previred.desafio.model.Empleado;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoRepository {

    public List<Empleado> findAll() throws SQLException {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT * FROM empleado";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()){
                empleados.add(mapResultSetToEmpleado(rs));
            }
        }
        return empleados;
    }

    public void save(Empleado emp) throws SQLException {
        String sql = "INSERT INTO empleado (nombre, apellido, rut, cargo, salario) VALUES (?, ?, ?, ?, ?)";

        try (Connection cnn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = cnn.prepareStatement(sql)){

            pstmt.setString(1, emp.getNombre());
            pstmt.setString(2, emp.getApellido());
            pstmt.setString(3, emp.getRut());
            pstmt.setString(4, emp.getCargo());
            pstmt.setDouble(5, emp.getSalario());
            pstmt.executeUpdate();
        }
    }

    public void delete (Long id) throws SQLException {
        String sql = "DELETE FROM empleado WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }

    public boolean existsByRut(String rut) throws SQLException {
        String sql = "SELECT COUNT(*) FROM empleado WHERE rut = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rut);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private Empleado mapResultSetToEmpleado(ResultSet rs) throws SQLException {
        return new Empleado(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("rut"),
                rs.getString("cargo"),
                rs.getDouble("salario"),
                rs.getDouble("bonos"),
                rs.getDouble("descuentos")
        );
    }
}
