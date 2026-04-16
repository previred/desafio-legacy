package cl.previred.rsanchez.demoprevired.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String nombre;
    @Column(name = "last_name")
    private String apellido;
    @Column(name = "rut_dni")
    private String rut;
    @Column(name = "type_rol")
    private String cargo;
    @Column(name = "salary")
    private int salario;
}
