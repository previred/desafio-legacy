package com.previred.desafio.repository;

import com.previred.desafio.config.ConnectionFactory;
import com.previred.desafio.model.Empleado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class EmpleadoRepository {

    private final ConnectionFactory connectionFactory;

    public EmpleadoRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public List<Empleado> findAll() {
        String sql = "SELECT id, nombre, apellido, rut_dni, cargo, salario_base, bono, descuentos "
                + "FROM empleados ORDER BY id";

        List<Empleado> empleados = new ArrayList<>();

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                empleados.add(mapRow(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("No fue posible obtener los empleados", exception);
        }

        return empleados;
    }

    public Empleado save(Empleado empleado) {
        String sql = "INSERT INTO empleados (nombre, apellido, rut_dni, cargo, salario_base, bono, descuentos) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, empleado.getNombre());
            statement.setString(2, empleado.getApellido());
            statement.setString(3, empleado.getRutDni());
            statement.setString(4, empleado.getCargo());
            statement.setBigDecimal(5, empleado.getSalarioBase());
            statement.setBigDecimal(6, empleado.getBono());
            statement.setBigDecimal(7, empleado.getDescuentos());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    empleado.setId(generatedKeys.getLong(1));
                }
            }

            return empleado;
        } catch (SQLException exception) {
            throw new IllegalStateException("No fue posible guardar el empleado", exception);
        }
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM empleados WHERE id = ?";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new IllegalStateException("No fue posible eliminar el empleado", exception);
        }
    }

    public boolean existsByRutDni(String rutDni) {
        String sql = "SELECT 1 FROM empleados WHERE UPPER(rut_dni) = UPPER(?)";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, rutDni);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("No fue posible validar el RUT/DNI", exception);
        }
    }

    private Empleado mapRow(ResultSet resultSet) throws SQLException {
        return new Empleado(
                resultSet.getLong("id"),
                resultSet.getString("nombre"),
                resultSet.getString("apellido"),
                resultSet.getString("rut_dni"),
                resultSet.getString("cargo"),
                resultSet.getBigDecimal("salario_base"),
                resultSet.getBigDecimal("bono"),
                resultSet.getBigDecimal("descuentos")
        );
    }
}
