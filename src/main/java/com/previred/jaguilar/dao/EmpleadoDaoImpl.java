package com.previred.jaguilar.dao;

import com.previred.jaguilar.exception.DaoException;
import com.previred.jaguilar.model.Empleado;
import com.previred.jaguilar.util.ConexionUtil;
import com.previred.jaguilar.util.LogUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDaoImpl implements EmpleadoDao {

    private static final String SQL_LISTAR = """
            SELECT id, nombre, apellido, rut_dni, cargo, salario, bono, descuentos
            FROM empleados
            ORDER BY id
            """;

    private static final String SQL_GUARDAR = """
            INSERT INTO empleados (nombre, apellido, rut_dni, cargo, salario, bono, descuentos)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SQL_ELIMINAR = """
            DELETE FROM empleados
            WHERE id = ?
            """;

    private static final String SQL_EXISTE_RUT = """
            SELECT 1
            FROM empleados
            WHERE rut_dni = ?
            LIMIT 1
            """;

    @Override
    public List<Empleado> listarTodos() {
        List<Empleado> empleados = new ArrayList<>();

        try (Connection connection = ConexionUtil.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Empleado empleado = new Empleado();
                empleado.setId(rs.getLong("id"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setApellido(rs.getString("apellido"));
                empleado.setRutDni(rs.getString("rut_dni"));
                empleado.setCargo(rs.getString("cargo"));
                empleado.setSalario(rs.getBigDecimal("salario"));
                empleado.setBono(rs.getBigDecimal("bono"));
                empleado.setDescuentos(rs.getBigDecimal("descuentos"));

                empleados.add(empleado);
            }

            LogUtil.info("DAO: Lista de empleados obtenida. Total: " + empleados.size());

        } catch (SQLException e) {
            LogUtil.error("DAO: Error al listar empleados", e);
            throw new DaoException("Error al listar empleados", e);
        }

        return empleados;
    }

    @Override
    public void guardar(Empleado empleado) {
        try (Connection connection = ConexionUtil.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(SQL_GUARDAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getRutDni());
            ps.setString(4, empleado.getCargo());
            ps.setBigDecimal(5, empleado.getSalario());
            ps.setBigDecimal(6, empleado.getBono() != null ? empleado.getBono() : BigDecimal.ZERO);
            ps.setBigDecimal(7, empleado.getDescuentos() != null ? empleado.getDescuentos() : BigDecimal.ZERO);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        empleado.setId(rs.getLong(1));
                    }
                }

                LogUtil.info("DAO: Empleado guardado correctamente. ID=" + empleado.getId());
            } else {
                LogUtil.warning("DAO: No se insertó el empleado.");
            }

        } catch (SQLException e) {
            LogUtil.error("DAO: Error al guardar empleado", e);
            throw new DaoException("Error al guardar empleado", e);
        }
    }

    @Override
    public boolean eliminarPorId(Long id) {
        try (Connection connection = ConexionUtil.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(SQL_ELIMINAR)) {

            ps.setLong(1, id);
            int filas = ps.executeUpdate();

            if (filas > 0) {
                LogUtil.info("DAO: Empleado eliminado correctamente. ID=" + id);
                return true;
            }

            LogUtil.warning("DAO: No se encontró empleado para eliminar. ID=" + id);
            return false;

        } catch (SQLException e) {
            LogUtil.error("DAO: Error al eliminar empleado", e);
            throw new DaoException("Error al eliminar empleado", e);
        }
    }

    @Override
    public boolean existePorRutDni(String rutDni) {
        try (Connection connection = ConexionUtil.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(SQL_EXISTE_RUT)) {

            ps.setString(1, rutDni);

            try (ResultSet rs = ps.executeQuery()) {
                boolean existe = rs.next();

                if (existe) {
                    LogUtil.warning("DAO: Ya existe RUT/DNI registrado: " + rutDni);
                }

                return existe;
            }

        } catch (SQLException e) {
            LogUtil.error("DAO: Error al validar RUT/DNI", e);
            throw new DaoException("Error al validar RUT/DNI", e);
        }
    }
}