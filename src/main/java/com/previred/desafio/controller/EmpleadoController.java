package com.previred.desafio.controller;

import com.previred.desafio.dto.EmpleadoRequest;
import com.previred.desafio.model.Empleado;
import com.previred.desafio.service.EmpleadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoController.class);

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    /**
     * GET /api/empleados
     * Retorna la lista completa de empleados en formato JSON.
     */
    @GetMapping
    public ResponseEntity<List<Empleado>> listarEmpleados() {
        log.info("GET /api/empleados");
        List<Empleado> empleados = empleadoService.obtenerTodos();
        return ResponseEntity.ok(empleados);
    }

    /**
     * POST /api/empleados
     * Agrega un nuevo empleado. Valida datos de entrada y reglas de negocio.
     */
    @PostMapping
    public ResponseEntity<Empleado> crearEmpleado(@Valid @RequestBody EmpleadoRequest request) {
        log.info("POST /api/empleados - RUT: {}", request.getRut());
        Empleado creado = empleadoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * DELETE /api/empleados/{id}
     * Elimina un empleado por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long id) {
        log.info("DELETE /api/empleados/{}", id);
        empleadoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
