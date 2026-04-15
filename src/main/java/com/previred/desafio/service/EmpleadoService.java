package com.previred.desafio.service;

import com.previred.desafio.dto.ApiError;
import com.previred.desafio.dto.EmpleadoRequest;
import com.previred.desafio.exception.BusinessException;
import com.previred.desafio.model.Empleado;
import com.previred.desafio.repository.EmpleadoRepository;
import com.previred.desafio.validation.EmpleadoValidator;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

/**
 * Orquesta reglas de negocio para la gestión de empleados.
 */
@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoValidator empleadoValidator;

    public EmpleadoService(EmpleadoRepository empleadoRepository, EmpleadoValidator empleadoValidator) {
        this.empleadoRepository = empleadoRepository;
        this.empleadoValidator = empleadoValidator;
    }

    /**
     * Obtiene todos los empleados registrados.
     *
     * @return listado de empleados
     */
    public List<Empleado> findAll() {
        return empleadoRepository.findAll();
    }

    /**
     * Valida y registra un nuevo empleado.
     *
     * @param request datos recibidos desde la API
     * @return empleado persistido con id generado
     */
    public Empleado create(EmpleadoRequest request) {
        List<ApiError> validationErrors = empleadoValidator.validate(request);
        if (!validationErrors.isEmpty()) {
            throw new BusinessException(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Validation failed",
                    validationErrors
            );
        }

        String normalizedRutDni = request.getRutDni().trim().toUpperCase();
        if (empleadoRepository.existsByRutDni(normalizedRutDni)) {
            throw new BusinessException(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Validation failed",
                    Collections.singletonList(ApiError.of(
                            "rutDni",
                            "DUPLICATE_RUT_DNI",
                            "El RUT/DNI ya existe",
                            request.getRutDni()
                    ))
            );
        }

        Empleado empleado = new Empleado();
        empleado.setNombre(request.getNombre().trim());
        empleado.setApellido(request.getApellido().trim());
        empleado.setRutDni(normalizedRutDni);
        empleado.setCargo(request.getCargo().trim());
        empleado.setSalarioBase(request.getSalarioBase());
        empleado.setBono(request.getBono());
        empleado.setDescuentos(request.getDescuentos());

        return empleadoRepository.save(empleado);
    }

    /**
     * Elimina un empleado existente por id.
     *
     * @param id identificador del empleado
     */
    public void deleteById(Long id) {
        if (id == null || id <= 0L) {
            throw new BusinessException(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Validation failed",
                    Collections.singletonList(ApiError.of(
                            "id",
                            "INVALID_ID",
                            "El id es obligatorio y debe ser mayor a cero",
                            id
                    ))
            );
        }

        boolean deleted = empleadoRepository.deleteById(id);
        if (!deleted) {
            throw new BusinessException(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Empleado no encontrado",
                    Collections.singletonList(ApiError.of(
                            "id",
                            "EMPLOYEE_NOT_FOUND",
                            "No existe un empleado con el id indicado",
                            id
                    ))
            );
        }
    }
}
