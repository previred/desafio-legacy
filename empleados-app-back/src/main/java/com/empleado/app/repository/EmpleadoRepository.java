package com.empleado.app.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.empleado.app.entity.EmpleadoEntity;

/**
 * Repositorio encargado del acceso a datos de la entidad Empleado.
 * Utiliza JdbcTemplate para interactuar directamente con la base de datos.
 * Autor: Cristian Palacios
 */
@Repository
public class EmpleadoRepository {

    @Autowired
    private JdbcTemplate jdbc;

    /**
     * Obtiene todos los empleados registrados en la base de datos.
     */
    public List<EmpleadoEntity> findAll() {
        return jdbc.query("SELECT * FROM empleados",
                (rs, i) -> EmpleadoEntity.builder()
                        .id(rs.getLong("id"))
                        .nombre(rs.getString("nombre"))
                        .apellido(rs.getString("apellido"))
                        .dni(rs.getString("dni"))
                        .cargo(rs.getString("cargo"))
                        .salario(rs.getDouble("salario"))
                        .bono(rs.getDouble("bono"))
                        .descuentos(rs.getDouble("descuentos"))
                        .build()
        );
    }

    /**
     * Guarda un nuevo empleado en la base de datos.
     */
    public void save(EmpleadoEntity e) {
        jdbc.update("INSERT INTO empleados(nombre, apellido, dni, cargo, salario,bono,descuentos) VALUES (?,?,?,?,?,?,?)",
                e.getNombre(), e.getApellido(), e.getDni(), e.getCargo(), e.getSalario(), e.getBono(), e.getDescuentos());
    }

    /**
     * Elimina un empleado por su ID.
     */
    public void delete(Long id) {
        jdbc.update("DELETE FROM empleados WHERE id = ?", id);
    }

    /**
     * Verifica si existe un empleado con el DNI indicado.
     */
    public boolean existsByDni(String dni) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(id) FROM empleados WHERE dni = ?",
                Integer.class,
                dni
        );
        return count != null && count > 0;
    }

    /**
     * Verifica si existe un empleado por su ID.
     */
    public boolean existsById(Long dni) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(id) FROM empleados WHERE id = ?",
                Integer.class,
                dni
        );
        return count != null && count > 0;
    }
}