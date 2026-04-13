package com.danielr.desafio.service;

import com.danielr.desafio.dto.EmployeeRequestDTO;
import com.danielr.desafio.dto.EmployeeResponseDTO;
import com.danielr.desafio.model.Employee;

import java.util.List;

public interface IEmployeeService {

    List<EmployeeResponseDTO> findAll();
    EmployeeResponseDTO save(EmployeeRequestDTO dto);
    boolean delete(Long id);

}
