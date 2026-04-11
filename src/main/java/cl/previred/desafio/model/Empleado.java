package cl.previred.desafio.model;

import java.math.BigDecimal;

/**
 * Entidad de dominio que representa a un empleado dentro del sistema.
 *
 * <p>Esta clase modela los datos fundamentales de un empleado incluyendo
 * informacion personal (nombre, apellido, RUT), datos laborales (cargo) y
 * compensacion financiera (salario, bono, descuentos).</p>
 *
 * <p>El RUT (Rol Único Tributario) es el identificador único de cada empleado
 * en el sistema chileno, y debe ser validado mediante {@code RutValidator}.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * Empleado empleado = new Empleado();
 * empleado.setNombre("Juan");
 * empleado.setApellido("Perez");
 * empleado.setRut("12345678-9");
 * empleado.setCargo("Desarrollador Senior");
 * empleado.setSalario(new BigDecimal("1500000"));
 * }</pre>
 *
 * @see cl.previred.desafio.util.RutValidator
 * @see cl.previred.desafio.repository.EmpleadoRepository
 * @since 1.0
 */
public class Empleado {

    /** Identificador unico del empleado en la base de datos. */
    private Long id;

    /** Nombre(s) del empleado. */
    private String nombre;

    /** Apellido(s) del empleado. */
    private String apellido;

    /**
     * RUT del empleado en formato XX.XXX.XXX-Y.
     * Debe ser unico en el sistema.
     */
    private String rut;

    /** Cargo o puesto de trabajo del empleado. */
    private String cargo;

    /**
     * Salario base del empleado.
     * Debe ser mayor o igual al salario minimo legal ($400,000).
     */
    private BigDecimal salario;

    /**
     * Bono adicional al salario.
     * No puede superar el 50% del salario.
     */
    private BigDecimal bono;

    /**
     * Descuentos aplicados al salario ( AFP, ISAPRE, etc.).
     * No pueden superar el monto del salario.
     */
    private BigDecimal descuentos;

    /**
     * Constructor por defecto.
     * Requerido para frameworks que usan reflexion (Spring, Jackson).
     */
    public Empleado() {
    }

    /**
     * Constructor con todos los campos.
     *
     * @param id         identificador unico del empleado
     * @param nombre     nombre(s) del empleado
     * @param apellido   apellido(s) del empleado
     * @param rut        RUT del empleado
     * @param cargo      cargo o puesto
     * @param salario    salario base
     * @param bono       bono adicional
     * @param descuentos descuentos aplicados
     */
    public Empleado(Long id, String nombre, String apellido, String rut, String cargo,
                    BigDecimal salario, BigDecimal bono, BigDecimal descuentos) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.cargo = cargo;
        this.salario = salario;
        this.bono = bono;
        this.descuentos = descuentos;
    }

    /**
     * Obtiene el identificador del empleado.
     *
     * @return el id del empleado, puede ser null si no ha sido persistido
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador del empleado.
     *
     * @param id identificador unico
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del empleado.
     *
     * @return el nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del empleado.
     *
     * @param nombre nombre(s) del empleado
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del empleado.
     *
     * @return el apellido
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido del empleado.
     *
     * @param apellido apellido(s) del empleado
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene el RUT del empleado.
     *
     * @return el RUT en formato XX.XXX.XXX-Y
     */
    public String getRut() {
        return rut;
    }

    /**
     * Establece el RUT del empleado.
     *
     * @param rut RUT en formato XX.XXX.XXX-Y
     */
    public void setRut(String rut) {
        this.rut = rut;
    }

    /**
     * Obtiene el cargo del empleado.
     *
     * @return el cargo
     */
    public String getCargo() {
        return cargo;
    }

    /**
     * Establece el cargo del empleado.
     *
     * @param cargo cargo o puesto de trabajo
     */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    /**
     * Obtiene el salario del empleado.
     *
     * @return el salario base
     */
    public BigDecimal getSalario() {
        return salario;
    }

    /**
     * Establece el salario del empleado.
     *
     * @param salario salario base (debe ser >= $400,000)
     */
    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    /**
     * Obtiene el bono del empleado.
     *
     * @return el bono, puede ser null
     */
    public BigDecimal getBono() {
        return bono;
    }

    /**
     * Establece el bono del empleado.
     *
     * @param bono bono adicional (no puede superar 50% del salario)
     */
    public void setBono(BigDecimal bono) {
        this.bono = bono;
    }

    /**
     * Obtiene los descuentos del empleado.
     *
     * @return los descuentos, puede ser null
     */
    public BigDecimal getDescuentos() {
        return descuentos;
    }

    /**
     * Establece los descuentos del empleado.
     *
     * @param descuentos monto de descuentos (no puede superar el salario)
     */
    public void setDescuentos(BigDecimal descuentos) {
        this.descuentos = descuentos;
    }
}
