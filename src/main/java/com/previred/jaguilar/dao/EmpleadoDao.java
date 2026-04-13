package com.previred.jaguilar.dao;

import com.previred.jaguilar.model.Empleado;
import java.util.List;

public interface EmpleadoDao {

    List<Empleado> listarTodos();

    void guardar(Empleado empleado);

    boolean eliminarPorId(Long id);

    boolean existePorRutDni(String rutDni);
}
