package com.previred.desafiolegacy.domain.port;

import com.previred.desafiolegacy.domain.exception.PersistenceException;
import com.previred.desafiolegacy.domain.model.Employee;

import java.util.List;

public interface EmployeeRepository {

    void guardar(Employee empleado)throws PersistenceException;

    List<Employee> listarTodos() throws PersistenceException;

    boolean existByRut(String rut)throws PersistenceException;

    void eliminar(Long id) throws PersistenceException;
}
