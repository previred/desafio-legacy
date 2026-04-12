package com.empleado.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.empleado.app.mapper.EmpleadoMapper;
import com.empleado.app.model.EmpleadoRequest;
import com.empleado.app.model.EmpleadoResponse;
import com.empleado.app.repository.EmpleadoRepository;
import com.empleado.app.validator.EmpleadoValidator;

/**
 * Servicio principal de Empleado.
 * Contiene la lógica de negocio y actúa como puente entre Controller y Repository.
 * Se encarga de coordinar validaciones, mapeos y operaciones de persistencia.
 * Autor: Cristian Palacios
 */
@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository repo;

    /**
     * Obtiene todos los empleados registrados en el sistema.
     * Convierte las entidades a DTO de respuesta para el cliente.
     */
    public List<EmpleadoResponse> getAll() {
        return repo.findAll()
                .stream()
                .map(EmpleadoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo empleado en el sistema.
     * Ejecuta validaciones avanzadas antes de persistir los datos.
     */
    public void create(EmpleadoRequest req) {
        EmpleadoValidator.validarAvanzados(req, repo);
        repo.save(EmpleadoMapper.toEntity(req));
    }

    /**
     * Elimina un empleado del sistema por su identificador.
     * Primero valida que el empleado exista antes de eliminarlo.
     */
    public void delete(Long id) {
        EmpleadoValidator.validarEmpleado(id, repo);
        repo.delete(id);
    }
}