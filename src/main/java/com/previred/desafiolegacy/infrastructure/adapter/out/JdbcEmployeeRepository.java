package com.previred.desafiolegacy.infrastructure.adapter.out;

import com.previred.desafiolegacy.domain.builder.EmployeeBuilder;
import com.previred.desafiolegacy.domain.exception.PersistenceException;
import com.previred.desafiolegacy.domain.model.Employee;
import com.previred.desafiolegacy.domain.port.EmployeeRepository;
import com.previred.desafiolegacy.infrastructure.config.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

import java.util.List;

public class JdbcEmployeeRepository implements EmployeeRepository {

    private final DatabaseConfig databaseConfig;

    public JdbcEmployeeRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @Override
    public void guardar(Employee emp){
        String sql = "INSERT INTO empleado (nombre, apellido, rut_dni, cargo, salario_base, bonos, descuentos) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getName());
            ps.setString(2, emp.getSurname());
            ps.setString(3, emp.getFiscalId());
            ps.setString(4, emp.getRole());
            ps.setBigDecimal(5, emp.getSalary().getBase());
            ps.setBigDecimal(6, emp.getSalary().getBonus() != null ? emp.getSalary().getBonus()  : BigDecimal.ZERO);
            ps.setBigDecimal(7, emp.getSalary().getDiscounts() != null ? emp.getSalary().getDiscounts(): BigDecimal.ZERO);

            ps.executeUpdate();
        }catch (SQLException ex){
            throw new PersistenceException(ex.getMessage());
        }
    }

    @Override
    public List<Employee> listarTodos(){
        List<Employee> empleados = new ArrayList<>();
        String sql = "SELECT * FROM empleado";

        try (Connection conn = databaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Employee emp = new EmployeeBuilder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("nombre"))
                        .surname(rs.getString("apellido"))
                        .fiscalId(rs.getString("rut_dni"))
                        .role(rs.getString("cargo"))
                        .baseSalary(rs.getBigDecimal("salario_base"))
                        .bonus(rs.getBigDecimal("bonos"))
                        .discounts(rs.getBigDecimal("descuentos"))
                        .build();
                empleados.add(emp);
            }
        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage());
        }
        return empleados;
    }

    @Override
    public boolean existByRut(String rut){
        String sql = "SELECT COUNT(*) FROM empleado WHERE rut_dni = ?";
        try (Connection conn = databaseConfig.getConnection();

             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rut);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }catch (SQLException ex){
            throw new PersistenceException(ex.getMessage());
        }
        return false;
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM empleado WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage());
        }
    }
}
