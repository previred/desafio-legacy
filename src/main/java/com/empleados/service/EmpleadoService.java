package com.empleados.service;

import com.empleados.api.generated.model.EmpleadoResponse;
import com.empleados.exception.business.RutDuplicadoException;
import com.empleados.mapper.EmpleadoMapper;
import com.empleados.model.Empleado;
import com.empleados.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Capa de logica de negocio para la gestion de empleados.
 * <p>
 * Orquesta la persistencia via {@link EmpleadoRepository}
 * y el mapeo Entity → DTO via {@link EmpleadoMapper}.
 * Valida la regla de RUT unico antes de la creacion.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmpleadoService {

    private final EmpleadoRepository repository;
    private final EmpleadoMapper mapper;

    /**
     * Lista todos los empleados registrados.
     *
     * @return lista de {@link EmpleadoResponse}, vacia si no hay registros
     */
    public List<EmpleadoResponse> listarEmpleados() {
        return mapper.toResponseList(repository.findAll());
    }

    /**
     * Crea un nuevo empleado tras validar que el RUT no este duplicado.
     *
     * @param empleado datos del empleado a crear (ya validado por Bean Validation)
     * @return DTO con los datos del empleado creado, incluyendo ID y salario liquido
     * @throws RutDuplicadoException si ya existe un empleado con el mismo RUT
     */
    public EmpleadoResponse crearEmpleado(Empleado empleado) {
        validarRutUnico(empleado.getRut());
        Empleado creado = repository.save(empleado);
        return mapper.toResponse(creado);
    }

    /**
     * Elimina un empleado por su ID si existe.
     *
     * @param id identificador del empleado
     * @return {@code true} si fue eliminado, {@code false} si no existia
     */
    public boolean eliminarEmpleado(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        log.info("Empleado eliminado con ID: {}", id);
        return true;
    }

    /**
     * Busca un empleado por su identificador.
     *
     * @param id identificador del empleado
     * @return {@link Optional} con el empleado si existe
     */
    public Optional<Empleado> buscarPorId(Long id) {
        return repository.findById(id);
    }

    /**
     * Valida que no exista otro empleado con el mismo RUT.
     *
     * @param rut RUT a verificar
     * @throws RutDuplicadoException si el RUT ya esta registrado
     */
    private void validarRutUnico(String rut) {
        if (repository.existsByRut(rut)) {
            throw new RutDuplicadoException("Ya existe un empleado con el RUT " + rut);
        }
    }
}
