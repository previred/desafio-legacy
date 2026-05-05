package cl.previred.dto;

public class EmpleadoResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String rutDni;
    private String cargo;
    private int salario;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getRutDni() { return rutDni; }
    public void setRutDni(String rutDni) { this.rutDni = rutDni; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public int getSalario() { return salario; }
    public void setSalario(int salario) { this.salario = salario; }
}
