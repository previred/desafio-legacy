package cl.previred.desafio.dto;

import java.math.BigDecimal;

/**
 * Objeto de Transferencia de Datos (DTO) para la creacion de empleados.
 *
 * <p>Esta clase actua como elRequest payload cuando un cliente desea crear
 * o actualizar un empleado a traves de la API REST. Contiene todos los
 * campos editables de un empleado junto con validaciones de negocio.</p>
 *
 * <p>El campo {@code id} no debe estar presente en la creacion, ya que
 * es generado por el sistema al persistir el empleado.</p>
 *
 * <p>Validaciones aplicadas:</p>
 * <ul>
 *   <li>Nombre: requerido, no vacio</li>
 *   <li>Apellido: requerido, no vacio</li>
 *   <li>RUT: requerido, formato valido, unico en el sistema</li>
 *   <li>Cargo: requerido</li>
 *   <li>Salario: requerido, >= $400,000</li>
 *   <li>Bono: opcional, <= 50% del salario</li>
 *   <li>Descuentos: opcionales, <= salario</li>
 * </ul>
 *
 * @see cl.previred.desafio.service.ValidationService
 * @see cl.previred.desafio.model.Empleado
 * @since 1.0
 */
public class EmpleadoRequest {

    /** Nombre(s) del empleado. */
    private String nombre;

    /** Apellido(s) del empleado. */
    private String apellido;

    /** RUT del empleado en formato XX.XXX.XXX-Y. */
    private String rut;

    /** Cargo o puesto de trabajo del empleado. */
    private String cargo;

    /** Salario base del empleado. */
    private BigDecimal salario;

    /** Bono adicional al salario (opcional). */
    private BigDecimal bono;

    /** Descuentos aplicados al salario (opcional). */
    private BigDecimal descuentos;

    /**
     * Constructor por defecto.
     * Requerido para deserializacion JSON de Jackson.
     */
    public EmpleadoRequest() {
        // Default constructor for Jackson deserialization
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
