package cl.previred.rsanchez.demoprevired.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String cargo;
    private int salario;
}
