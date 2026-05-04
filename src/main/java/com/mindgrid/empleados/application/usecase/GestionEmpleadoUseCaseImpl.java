package com.mindgrid.empleados.application.usecase;

import com.mindgrid.empleados.domain.exception.BusinessException;
import com.mindgrid.empleados.domain.model.Empleado;
import com.mindgrid.empleados.domain.repository.EmpleadoRepository;
import com.mindgrid.empleados.domain.service.ValidacionService;

import java.util.List;

public class GestionEmpleadoUseCaseImpl implements GestionEmpleadoUseCase {

    private final EmpleadoRepository repository;
    private final ValidacionService validacionService;

    public GestionEmpleadoUseCaseImpl(EmpleadoRepository repository, ValidacionService validacionService) {
        this.repository = repository;
        this.validacionService = validacionService;
    }

    @Override
    public List<Empleado> listarEmpleados() {
        return repository.findAll();
    }

    @Override
    public Empleado agregarEmpleado(Empleado empleado) {
        validacionService.validar(empleado);

        if (repository.existsByRut(empleado.getRut())) {
            throw new BusinessException("Ya existe un empleado registrado con el RUT: " + empleado.getRut());
        }

        return repository.save(empleado);
    }

    @Override
    public void eliminarEmpleado(Long id) {
        boolean eliminado = repository.deleteById(id);
        if (!eliminado) {
            throw new BusinessException("No se encontró ningún empleado con el ID: " + id);
        }
    }
}
