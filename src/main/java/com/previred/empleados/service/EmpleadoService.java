package com.previred.empleados.service;

import com.previred.empleados.entity.EmpleadoEntity;
import com.previred.empleados.exception.BusinessValidationException;
import com.previred.empleados.exception.EmpleadoNotFoundException;
import com.previred.empleados.mapper.EmpleadoMapper;
import com.previred.empleados.model.EmpleadoModel;
import com.previred.empleados.repository.IEmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpleadoService implements IEmpleadoService {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoService.class);
    private static final double SALARIO_MINIMO = 400000.0;
    private static final double PORCENTAJE_MAXIMO_BONOS = 0.5;

    private final IEmpleadoRepository empleadoRepository;

    public EmpleadoService(IEmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public List<EmpleadoModel> listarEmpleados() {
        return empleadoRepository.findAll().stream()
                .map(EmpleadoMapper::toModel)
                .collect(Collectors.toList());
    }

    public EmpleadoModel crearEmpleado(EmpleadoModel model) {
        validar(model);
        EmpleadoEntity entity = EmpleadoMapper.toEntity(model);
        EmpleadoEntity saved = empleadoRepository.save(entity);
        log.info("Empleado creado con ID: {}", saved.getId());
        return EmpleadoMapper.toModel(saved);
    }

    public void eliminarEmpleado(Long id) {
        if (!empleadoRepository.deleteById(id)) {
            throw new EmpleadoNotFoundException(id);
        }
        log.info("Empleado eliminado con ID: {}", id);
    }

    private void validar(EmpleadoModel model) {
        List<String> errores = new ArrayList<>();

        if (isBlank(model.getNombre())) {
            errores.add("El nombre es obligatorio");
        }
        if (isBlank(model.getApellido())) {
            errores.add("El apellido es obligatorio");
        }
        if (isBlank(model.getRut())) {
            errores.add("El RUT/DNI es obligatorio");
        } else {
            if (!validarFormatoRut(model.getRut())) {
                errores.add("El formato del RUT/DNI no es válido (ej: 12345678-5)");
            }
            if (empleadoRepository.existsByRut(model.getRut())) {
                errores.add("Ya existe un empleado con el RUT/DNI: " + model.getRut());
            }
        }
        if (isBlank(model.getCargo())) {
            errores.add("El cargo es obligatorio");
        }
        if (model.getSalarioBase() == null || model.getSalarioBase() < SALARIO_MINIMO) {
            errores.add("El salario base no puede ser menor a $400,000");
        }
        if (model.getBonos() != null && model.getSalarioBase() != null
                && model.getBonos() > model.getSalarioBase() * PORCENTAJE_MAXIMO_BONOS) {
            errores.add("Los bonos no pueden superar el 50% del salario base");
        }
        if (model.getDescuentos() != null && model.getSalarioBase() != null
                && model.getDescuentos() > model.getSalarioBase()) {
            errores.add("El total de descuentos no puede ser mayor al salario base");
        }

        if (!errores.isEmpty()) {
            log.warn("Validación fallida: {}", errores);
            throw new BusinessValidationException(errores);
        }
    }

    private boolean validarFormatoRut(String rut) {
        return rut.matches("^\\d{7,8}-[\\dkK]$");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
