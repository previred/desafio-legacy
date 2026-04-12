package com.empleados.repository;

import com.empleados.model.Empleado;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad {@link Empleado}.
 * <p>
 * Implementado con {@link JdbcTemplate} contra la base de datos
 * H2 en memoria.
 * </p>
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class EmpleadoRepository {

    private final JdbcTemplate jdbcTemplate;

    /** Mapea cada fila del ResultSet a un objeto {@link Empleado}. */
    private static final RowMapper<Empleado> ROW_MAPPER = (rs, rowNum) ->
        Empleado.builder()
            .id(rs.getLong("id"))
            .nombre(rs.getString("nombre"))
            .apellido(rs.getString("apellido"))
            .rut(rs.getString("rut"))
            .cargo(rs.getString("cargo"))
            .salario(rs.getLong("salario"))
            .bono(rs.getLong("bono"))
            .descuentos(rs.getLong("descuentos"))
            .build();

    /**
     * Obtiene todos los empleados ordenados por ID.
     *
     * @return lista de empleados, vacia si no hay registros
     */
    public List<Empleado> findAll() {
        return jdbcTemplate.query(
            "SELECT id, nombre, apellido, rut, cargo, salario, bono, descuentos FROM empleados ORDER BY id",
            ROW_MAPPER
        );
    }

    /**
     * Busca un empleado por su identificador.
     *
     * @param id identificador del empleado
     * @return {@link Optional} con el empleado si existe, vacio si no
     */
    public Optional<Empleado> findById(Long id) {
        List<Empleado> results = jdbcTemplate.query(
            "SELECT id, nombre, apellido, rut, cargo, salario, bono, descuentos FROM empleados WHERE id = ?",
            ROW_MAPPER, id
        );
        return results.stream().findFirst();
    }

    /**
     * Verifica si ya existe un empleado con el RUT indicado.
     *
     * @param rut RUT/DNI a verificar
     * @return {@code true} si el RUT ya esta registrado
     */
    public boolean existsByRut(String rut) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM empleados WHERE rut = ?", Integer.class, rut
        );
        return count != null && count > 0;
    }

    /**
     * Verifica si existe un empleado con el ID indicado.
     *
     * @param id identificador a verificar
     * @return {@code true} si el empleado existe
     */
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM empleados WHERE id = ?", Integer.class, id
        );
        return count != null && count > 0;
    }

    /**
     * Persiste un nuevo empleado y asigna el ID autogenerado.
     *
     * @param empleado empleado a guardar (sin ID)
     * @return el mismo empleado con el ID asignado
     */
    public Empleado save(Empleado empleado) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO empleados (nombre, apellido, rut, cargo, salario, bono, descuentos) VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getRut());
            ps.setString(4, empleado.getCargo());
            ps.setLong(5, empleado.getSalario());
            ps.setLong(6, empleado.getBono());
            ps.setLong(7, empleado.getDescuentos());
            return ps;
        }, keyHolder);

        empleado.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Empleado creado con ID: {}", empleado.getId());
        return empleado;
    }

    /**
     * Elimina un empleado por su identificador.
     *
     * @param id identificador del empleado a eliminar
     */
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM empleados WHERE id = ?", id);
        log.info("Empleado eliminado con ID: {}", id);
    }
}
