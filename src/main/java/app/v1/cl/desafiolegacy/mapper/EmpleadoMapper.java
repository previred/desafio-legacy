package app.v1.cl.desafiolegacy.mapper;

import app.v1.cl.desafiolegacy.dto.EmpleadoDTO;
import app.v1.cl.desafiolegacy.model.Empleado;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EmpleadoMapper {

    public EmpleadoDTO toDTO(Empleado entity) {
        return EmpleadoDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .rut(entity.getRut())
                .cargo(entity.getCargo())
                .salario(entity.getSalario())
                .bonos(entity.getBonos())
                .descuentos(entity.getDescuentos())
                .build();
    }

    public Empleado toEntity(EmpleadoDTO dto) {
        return Empleado.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .rut(dto.getRut())
                .cargo(dto.getCargo())
                .salario(dto.getSalario())
                .bonos(dto.getBonos())
                .descuentos(dto.getDescuentos())
                .build();
    }

    public List<EmpleadoDTO> toDTOList(List<Empleado> entities) {
        return entities.stream()
                .map(EmpleadoMapper::toDTO)
                .collect(Collectors.toList());
    }
}
