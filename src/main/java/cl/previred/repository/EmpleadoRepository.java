package cl.previred.repository;

import cl.previred.model.Empleado;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository {
    List<Empleado> findAll();
    Empleado save(Empleado empleado);
    Optional<Empleado> findById(Long id);
    Optional<Empleado> findByRutDni(String rutDni);
    void deleteById(Long id);
}
