package cl.maraneda.previred.model;

public class Usuario{
    private int id;
    private String nombre;
    private String apellido;
    private String rut;
    private String cargo;
    private int salario;
    private int monto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public int getSalario() {
        return salario;
    }

    public void setSalario(int salario) {
        this.salario = salario;
    }

    @Override
    public boolean equals(Object other){
        if(other == null){
            return false;
        }
        if(other == this){
            return true;
        }
        if(other instanceof Usuario uother){
            return rut.equalsIgnoreCase(uother.getRut());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }
}
