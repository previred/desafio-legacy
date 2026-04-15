package com.desafio.repository;

import com.desafio.model.Empleado;
import com.desafio.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoRepository {
    public List<Empleado> findAll() {
        List<Empleado> lista = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM empleados")) {

            while (rs.next()) {
                Empleado e = new Empleado();
                e.setId(rs.getLong("id"));
                e.setNombre(rs.getString("nombre"));
                e.setApellido(rs.getString("apellido"));
                e.setDni(rs.getString("dni"));
                e.setCargo(rs.getString("cargo"));
                e.setSalario(rs.getDouble("salario"));
                e.setBono(rs.getDouble("bono"));
                e.setDescuentos(rs.getDouble("descuentos"));

                lista.add(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void save(Empleado e) {
        String sql = "INSERT INTO empleados (nombre, apellido, dni, cargo, salario, bono, descuentos) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, e.getNombre());
            stmt.setString(2, e.getApellido());
            stmt.setString(3, e.getDni());
            stmt.setString(4, e.getCargo());
            stmt.setDouble(5, e.getSalario());
            stmt.setDouble(6, e.getBono());
            stmt.setDouble(7, e.getDescuentos());

            stmt.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM empleados WHERE id=" + id;

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean existsByDni(String dni) {
        String sql = "SELECT COUNT(*) FROM empleados WHERE dni = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dni);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
