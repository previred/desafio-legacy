package com.desafio.service;

import com.desafio.model.Empleado;
import com.desafio.repository.EmpleadoRepository;

import java.util.List;

public class EmpleadoService {
    private EmpleadoRepository repo = new EmpleadoRepository();

    public List<Empleado> list() {
        return repo.findAll();
    }

    public void create(Empleado e) {
        validations(e);
        repo.save(e);
    }

    private void validations(Empleado e) {
        // Validación de regla de negocio: dni duplicado
        if (repo.existsByDni(e.getDni())) {
            throw new RuntimeException("DNI duplicado");
        }

        // Validación de regla de negocio: salario mínimo requerido
        if (e.getSalario() < 400000) {
            throw new RuntimeException("Salario inválido");
        }

        // Validación de regla de negocio: bono no supere el 50% del salario
        if (e.getBono() > e.getSalario() * 0.5) {
            throw new RuntimeException("Bono supera el 50%");
        }

        // Validación de regla de negocio: total de descuentos no puede superar al salario
        if (e.getDescuentos() > e.getSalario()) {
            throw new RuntimeException("Descuentos superan salario");
        }
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
