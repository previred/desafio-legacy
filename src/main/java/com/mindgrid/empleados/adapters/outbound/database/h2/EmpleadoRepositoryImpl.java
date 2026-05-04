package com.mindgrid.empleados.adapters.outbound.database.h2;

import com.mindgrid.empleados.adapters.outbound.database.entity.EmpleadoEntity;
import com.mindgrid.empleados.adapters.outbound.database.h2.mapper.EmpleadoDbMapper;
import com.mindgrid.empleados.domain.exception.BusinessException;
import com.mindgrid.empleados.domain.model.Empleado;
import com.mindgrid.empleados.domain.repository.EmpleadoRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EmpleadoRepositoryImpl implements EmpleadoRepository {

    private static final Logger LOGGER = Logger.getLogger(EmpleadoRepositoryImpl.class.getName());

    private static final String SQL_FIND_ALL =
        "SELECT id, nombre, apellido, rut, cargo, salario_base, bono, descuentos FROM empleados";
    private static final String SQL_FIND_BY_ID =
        "SELECT id, nombre, apellido, rut, cargo, salario_base, bono, descuentos FROM empleados WHERE id = ?";
    private static final String SQL_INSERT =
        "INSERT INTO empleados (nombre, apellido, rut, cargo, salario_base, bono, descuentos) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_DELETE =
        "DELETE FROM empleados WHERE id = ?";
    private static final String SQL_EXISTS_RUT =
        "SELECT COUNT(*) FROM empleados WHERE rut = ?";

    private final DataSource dataSource;

    public EmpleadoRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Empleado> findAll() {
        List<EmpleadoEntity> entities = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                entities.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.severe("Error al consultar empleados: " + e.getMessage());
            throw new RuntimeException("Error al consultar empleados", e);
        }

        return entities.stream().map(EmpleadoDbMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Empleado> findById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(EmpleadoDbMapper.toDomain(mapRow(rs)));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error al buscar empleado por ID: " + e.getMessage());
            throw new RuntimeException("Error al buscar empleado", e);
        }
        return Optional.empty();
    }

    @Override
    public Empleado save(Empleado empleado) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getRut());
            ps.setString(4, empleado.getCargo());
            ps.setDouble(5, empleado.getSalarioBase());
            ps.setDouble(6, empleado.getBono());
            ps.setDouble(7, empleado.getDescuentos());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long generatedId = keys.getLong(1);
                    return Empleado.builder()
                        .id(generatedId)
                        .nombre(empleado.getNombre())
                        .apellido(empleado.getApellido())
                        .rut(empleado.getRut())
                        .cargo(empleado.getCargo())
                        .salarioBase(empleado.getSalarioBase())
                        .bono(empleado.getBono())
                        .descuentos(empleado.getDescuentos())
                        .build();
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                throw new BusinessException(
                    "Ya existe un empleado registrado con el RUT: " + empleado.getRut()
                );
            }
            LOGGER.severe("Error al guardar empleado: " + e.getMessage());
            throw new RuntimeException("Error al guardar empleado", e);
        }
        throw new RuntimeException("No se pudo obtener el ID generado para el nuevo empleado");
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.severe("Error al eliminar empleado: " + e.getMessage());
            throw new RuntimeException("Error al eliminar empleado", e);
        }
    }

    @Override
    public boolean existsByRut(String rut) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_RUT)) {

            ps.setString(1, rut);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.severe("Error al verificar RUT duplicado: " + e.getMessage());
            throw new RuntimeException("Error al verificar RUT", e);
        }
    }

    private EmpleadoEntity mapRow(ResultSet rs) throws SQLException {
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setId(rs.getLong("id"));
        entity.setNombre(rs.getString("nombre"));
        entity.setApellido(rs.getString("apellido"));
        entity.setRut(rs.getString("rut"));
        entity.setCargo(rs.getString("cargo"));
        entity.setSalarioBase(rs.getDouble("salario_base"));
        entity.setBono(rs.getDouble("bono"));
        entity.setDescuentos(rs.getDouble("descuentos"));
        return entity;
    }
}
