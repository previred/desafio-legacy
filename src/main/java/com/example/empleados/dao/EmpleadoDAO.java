package com.example.empleados.dao;

import com.example.empleados.model.Empleado;
import com.example.empleados.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    public List<Empleado> findAll() throws SQLException {
        List<Empleado> empleados = new ArrayList<>();

        String sql = "SELECT * FROM empleados";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Empleado e = new Empleado();
                e.setId(rs.getLong("id"));
                e.setNombre(rs.getString("nombre"));
                e.setApellido(rs.getString("apellido"));
                e.setRut(rs.getString("rut"));
                e.setCargo(rs.getString("cargo"));
                e.setSalario(rs.getDouble("salario"));

                empleados.add(e);
            }
        }

        return empleados;
    }

    public void save(Empleado e) throws SQLException {
        String sql =
                "INSERT INTO empleados (nombre, apellido, rut, cargo, salario) " +
                        "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellido());
            ps.setString(3, e.getRut());
            ps.setString(4, e.getCargo());
            ps.setDouble(5, e.getSalario());

            ps.executeUpdate();
        }
    }

    public boolean deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM empleados WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existsByRut(String rut) throws SQLException {
        String sql = "SELECT COUNT(*) FROM empleados WHERE rut = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rut);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
}
