package cl.previred.desafio.repository;

import cl.previred.desafio.model.Empleado;
import java.util.List;

public interface EmpleadoRepositoryPort {
    List<Empleado> findAll();
    Empleado findById(Long id);
    boolean existsByRut(String rut);
    Empleado save(Empleado empleado);
    boolean deleteById(Long id);
}