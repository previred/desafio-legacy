package com.danielr.desafio.service.impl;

import com.danielr.desafio.dto.EmployeeRequestDTO;
import com.danielr.desafio.dto.EmployeeResponseDTO;
import com.danielr.desafio.exception.BusinessException;
import com.danielr.desafio.model.Employee;
import com.danielr.desafio.repository.EmployeeRepository;
import com.danielr.desafio.service.IEmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private static final BigDecimal MIN_SALARY = new BigDecimal("400000");
    private static final BigDecimal MAX_BONUS = new BigDecimal("0.50");

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<EmployeeResponseDTO> findAll() {
        logger.debug("Obteniendo lista de empleados........");
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDTO save(EmployeeRequestDTO dto) {

        logger.debug("Guardando empleado: {} - {}", dto.name() + " " + dto.lastname(), dto.dni());

        List<String> errors = new ArrayList<>();

        // validamos - No permitir salarios base menores a $400,000.
        if (hasInvalidMinSalary(dto.salary())) {
            errors.add("El salario debe ser mayor a $400.000");
        }

        // validamos - Bonos no pueden superar el 50% del salario base.
        if (hasInvalidMaxBonus(dto.salary(), dto.bonus())) {
            errors.add("El bono no debe ser superior al 50% del salario base");
        }

        if (hasInvalidDeductions(dto.salary(), dto.deductions())) {
            errors.add("Los descuentos no pueden superar el salario base");
        }

        if (employeeRepository.existsByDni(dto.dni())) {
            errors.add("Ya existe un empleado con el RUT: " + dto.dni());
        }

        if (!errors.isEmpty()) {
            logger.warn("Validaciones fallidas para RUT {}: {}", dto.dni(), errors);
            throw new BusinessException(errors);
        }

        try {

            Employee savedEmployee = employeeRepository.save(toEntity(dto));
            logger.debug("Empleado creado exitosamente con nombre: {}",
                    savedEmployee.getFirstName() + " " + savedEmployee.getLastName());
            return toResponseDTO(savedEmployee);
        } catch (BusinessException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            logger.error("Violacion de constraint al crear empleado con DNI: {}", dto.dni());
            throw new BusinessException("Ya existe un empleado con el RUT: " +  dto.dni());
        } catch (RuntimeException e) {
            logger.error("Error inesperado al guardar empleado con RUT: {} - {}", dto.dni(), e.getMessage());
            throw new BusinessException("Error inesperado al guardar el empleado");
        }
    }

    @Override
    public boolean delete(Long id) {
        logger.debug("Eliminando empleado con id: {}", id);

        boolean deleted = employeeRepository.deleteById(id);

        if (!deleted) {
            logger.warn("Empleado no encontrado con id: {}", id);
            throw new BusinessException("Empleado no encontrado con id: " + id);
        }

        logger.debug("Empleado eliminado correctamente con id: {}", id);
        return deleted;
    }

    private EmployeeResponseDTO toResponseDTO(Employee e) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dateCreate = e.getCreatedAt().format(formatter);

        return new EmployeeResponseDTO(
                e.getId(),
                e.getFirstName(),
                e.getLastName(),
                e.getTaxId(),
                e.getPosition(),
                e.getBaseSalary(),
                e.getBonus(),
                e.getDeductions(),
                dateCreate
        );
    }

    private Employee toEntity(EmployeeRequestDTO dto) {
        Employee e = new Employee();
        e.setFirstName(dto.name());
        e.setLastName(dto.lastname());
        e.setTaxId(dto.dni());
        e.setPosition(dto.position());
        e.setBaseSalary(dto.salary());
        e.setBonus(dto.bonus() != null ? dto.bonus() : BigDecimal.ZERO);
        e.setDeductions(dto.deductions() != null ? dto.deductions() : BigDecimal.ZERO);
        return e;
    }

    private boolean hasInvalidMinSalary(BigDecimal salary) {
       return salary.compareTo(MIN_SALARY) < 0;
    }

    private boolean hasInvalidMaxBonus(BigDecimal salary, BigDecimal bonus) {
        if (bonus!=null && bonus.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal limit = salary.multiply(MAX_BONUS);
            return bonus.compareTo(limit) > 0;
        }
        return false;
    }

    private boolean hasInvalidDeductions(BigDecimal salary, BigDecimal deductions) {
        if (deductions!=null && deductions.compareTo(BigDecimal.ZERO) > 0) {
            return deductions.compareTo(salary) > 0;
        }
        return false;
    }

}
