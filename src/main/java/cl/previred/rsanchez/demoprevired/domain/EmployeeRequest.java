package cl.previred.rsanchez.demoprevired.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeRequest {

    private String rut;
    private String nombre;
    private String apellido;
    private String cargo;
    private int salario;
    private int bonos;
    private int descuentos;

}
