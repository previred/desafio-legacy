package com.empleado.app.validator;

import java.util.ArrayList;
import java.util.List;

import com.empleado.app.exception.ApiException;
import com.empleado.app.model.EmpleadoRequest;
import com.empleado.app.repository.EmpleadoRepository;

/**
 * Clase encargada de validar las reglas de negocio del empleado.
 * Centraliza validaciones básicas y avanzadas antes de persistir datos.
 * Lanza excepciones controladas en caso de errores.
 * Autor: Cristian Palacios
 */
public class EmpleadoValidator {

    /**
     * Valida los campos básicos obligatorios del empleado.
     * Se asegura de que los datos mínimos estén presentes antes de continuar.
     */
    public static void validarCamposBasicos(EmpleadoRequest req) {

        List<String> errors = new ArrayList<>();

        if (req.getNombre() == null || req.getNombre().trim().isEmpty()) {
            errors.add("El nombre es obligatorio");
        }

        if (req.getApellido() == null || req.getApellido().trim().isEmpty()) {
            errors.add("El apellido es obligatorio");
        }

        if (req.getDni() == null || req.getDni().trim().isEmpty()) {
            errors.add("El DNI es obligatorio");
        }

        if (req.getCargo() == null || req.getCargo().trim().isEmpty()) {
            errors.add("El cargo es obligatorio");
        }

        if (req.getSalario() == null) {
            errors.add("El salario es obligatorio");
        }

        if (!errors.isEmpty()) {
            throw new ApiException(errors);
        }
    }

    /**
     * Valida reglas de negocio avanzadas del empleado.
     * Incluye validaciones de salario, bonos, descuentos y duplicidad de DNI.
     */
    public static void validarAvanzados(EmpleadoRequest req, EmpleadoRepository repo) {

        List<String> errors = new ArrayList<>();

        double salario = req.getSalario() != null ? req.getSalario() : 0;
        double bono = req.getBono() != null ? req.getBono() : 0;
        double descuentos = req.getDescuentos() != null ? req.getDescuentos() : 0;

        // Validación de salario mínimo permitido
        if (req.getSalario() == null || salario < 400000) {
            errors.add("El salario no puede ser menor a 400000");
        }

        // Validación de bono máximo permitido (50% del salario base)
        if (bono > salario * 0.5) {
            errors.add("El bono no puede superar el 50% del salario base");
        }

        // Validación de descuentos máximos
        if (descuentos > salario) {
            errors.add("Los descuentos no pueden ser mayores al salario base");
        }

        // Validación de duplicidad de DNI en base de datos
        if (repo.existsByDni(req.getDni())) {
            errors.add("Ya existe un empleado con este DNI");
        }

        if (!errors.isEmpty()) {
            throw new ApiException(errors);
        }
    }

    /**
     * Valida que un empleado exista antes de realizar operaciones como eliminación.
     */
    public static void validarEmpleado(Long id, EmpleadoRepository repo) {

        List<String> errors = new ArrayList<>();

        if (!repo.existsById(id)) {
            errors.add("El empleado que intenta borrar no existe");
        }

        if (!errors.isEmpty()) {
            throw new ApiException(errors);
        }
    }
}