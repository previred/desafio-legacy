package app.v1.cl.desafiolegacy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empleado {

    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String cargo;
    private BigDecimal salario;

    @Builder.Default
    private BigDecimal bonos = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal descuentos = BigDecimal.ZERO;
}
