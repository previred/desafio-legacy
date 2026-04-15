package com.previred.desafio.service;

import com.previred.desafio.exception.BusinessException;
import com.previred.desafio.model.Empleado;
import com.previred.desafio.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class EmpleadoService {

    private final EmpleadoRepository repository;

    public EmpleadoService(EmpleadoRepository repository){
        this.repository = repository;
    }

    public List<Empleado> listarTodos() throws SQLException {
        return repository.findAll();
    }

    public void registrarEmpleado(Empleado emp) throws BusinessException, SQLException {
        if (emp.getNombre() == null || emp.getNombre().isEmpty() || emp.getRut() == null || emp.getRut().isEmpty()) {
            throw new BusinessException("Todos los campos obligatorios deben estar completos.");
        }

        if (emp.getSalario() < 400000) {
            throw new BusinessException("El salario base no puede ser menor a $400.000.");
        }

        if (repository.existsByRut(emp.getRut())) {
            throw new BusinessException("Ya existe un empleado registrado con el RUT: " + emp.getRut());
        }

        double limiteBono = emp.getSalario() * 0.5;
        if (emp.getBonos() > limiteBono) {
            throw new BusinessException("Los bonos (" + emp.getBonos() +
                    ") no pueden superar el 50% del salario base (Máx: " + limiteBono + ").");
        }

        if (emp.getDescuentos() > emp.getSalario()) {
            throw new BusinessException("El total de descuentos no puede ser mayor al salario base.");
        }

        repository.save(emp);
    }

    public void eliminarEmpleado (Long id) throws SQLException {
        repository.delete(id);
    }
}
