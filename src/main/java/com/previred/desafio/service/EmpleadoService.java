package com.previred.desafio.service;

import com.previred.desafio.dto.EmpleadoRequest;
import com.previred.desafio.exception.EmpleadoNotFoundException;
import com.previred.desafio.exception.RutDuplicadoException;
import com.previred.desafio.model.Empleado;
import com.previred.desafio.repository.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EmpleadoService {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoService.class);
    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("400000");

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public List<Empleado> obtenerTodos() {
        log.info("Obteniendo lista de todos los empleados");
        return empleadoRepository.findAll();
    }

    public Empleado crear(EmpleadoRequest request) {
        log.info("Creando nuevo empleado con RUT: {}", request.getRut());

        validarSalarioMinimo(request.getSalario());
        validarBono(request.getBono(), request.getSalario());
        validarDescuento(request.getDescuento(), request.getSalario());
        validarRutUnico(request.getRut());

        Empleado empleado = new Empleado(
                null,
                request.getNombre().trim(),
                request.getApellido().trim(),
                request.getRut().trim(),
                request.getCargo().trim(),
                request.getSalario(),
                request.getBono(),
                request.getDescuento()
        );

        Empleado guardado = empleadoRepository.save(empleado);
        log.info("Empleado creado con ID: {}", guardado.getId());
        return guardado;
    }

    public void eliminar(Long id) {
        log.info("Eliminando empleado con ID: {}", id);

        boolean eliminado = empleadoRepository.deleteById(id);
        if (!eliminado) {
            throw new EmpleadoNotFoundException(id);
        }

        log.info("Empleado con ID {} eliminado correctamente", id);
    }

    // ── Validaciones privadas de negocio ──────────────────────────────────────

    private void validarSalarioMinimo(BigDecimal salario) {
        if (salario.compareTo(SALARIO_MINIMO) < 0) {
            throw new IllegalArgumentException(
                    "El salario base no puede ser menor a $400.000"
            );
        }
    }

    private void validarBono(BigDecimal bono, BigDecimal salario) {
        // Bono no puede superar el 50% del salario base
        BigDecimal limiteMaximoBono = salario.multiply(new BigDecimal("0.50"));
        if (bono.compareTo(limiteMaximoBono) > 0) {
            throw new IllegalArgumentException(
                    "El bono no puede superar el 50% del salario base ($" +
                    limiteMaximoBono.setScale(0, java.math.RoundingMode.HALF_UP) + ")"
            );
        }
    }

    private void validarDescuento(BigDecimal descuento, BigDecimal salario) {
        // Total de descuentos no puede ser mayor al salario base
        if (descuento.compareTo(salario) > 0) {
            throw new IllegalArgumentException(
                    "El total de descuentos no puede ser mayor al salario base"
            );
        }
    }

    private void validarRutUnico(String rut) {
        if (empleadoRepository.existsByRut(rut)) {
            throw new RutDuplicadoException(rut);
        }
    }
}
