package app.v1.cl.desafiolegacy.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoDTO {

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
