package com.desafio.legacy.services.impl;

import com.desafio.legacy.entities.Empleado;
import com.desafio.legacy.models.EmpleadoDTO;
import com.desafio.legacy.repositories.EmpleadoRepository;
import com.desafio.legacy.services.EmpleadoService;
import com.desafio.legacy.validations.ValidacionesCampos;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {
    private final EmpleadoRepository empleadoRepository;
    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public List<EmpleadoDTO> listar() {
        List<Empleado> listarEmpleados = empleadoRepository.listarEmpleados();

        return listarEmpleados.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void guardar(Empleado empleado) throws Exception {
        ValidacionesCampos.validacionCampos(empleado);

        String rutLimpio = empleado.getRut_dni().replace(".", "").replace("-", "").toUpperCase();
        empleado.setRut_dni(rutLimpio);

        Optional<Empleado> rutExistente = empleadoRepository.buscarPorRut(rutLimpio);

        if (rutExistente.isPresent()) {
            throw new Exception("El RUT ya existe en nuestros registros.");
        }

        empleadoRepository.registrarEmpleado(empleado);
    }

    @Override
    public void eliminar(Long id) throws SQLException {
        empleadoRepository.eliminarEmpleado(id);
    }

    private EmpleadoDTO convertToDTO(Empleado entity) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setRutDni(entity.getRut_dni());
        dto.setCargo(entity.getCargo());
        dto.setSalario(entity.getSalario());
        dto.setBono(entity.getBono());
        dto.setDescuento(entity.getDescuento());
        return dto;
    }
}
