package cl.maraneda.previred.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class UsuarioDTO implements Serializable {
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
    public String toString() {
        String str = "{ %s }";
        StringBuilder sb = new StringBuilder();
        AtomicBoolean isFirst = new AtomicBoolean(Boolean.TRUE);
        Map<String, Object> campos = Map.of(
                "id", id,
                "nombre", nombre,
                "apellido", apellido,
                "rut", rut,
                "cargo", cargo,
                "salario", salario,
                "monto", monto
        );

        campos.forEach((k, v) -> {
            sb.append(isFirst.get() ? "" : ",")
                    .append(String.format("\"%s\":", k))
                    .append(String.format(v instanceof String ? "\"%s\"" : "%s", v.toString()));
            if (isFirst.get()) {
                isFirst.set(Boolean.FALSE);
            }
        });
        return String.format(str, sb);
    }

    public static String toString(List<UsuarioDTO> us) {
        if (us == null || us.isEmpty()) {
            return "[]";
        }
        String str = "[ %s ]";
        StringBuilder sb = new StringBuilder();
        AtomicBoolean isFirst = new AtomicBoolean(Boolean.TRUE);
        us.forEach(x -> {
            sb.append(isFirst.get() ? "" : ",").append(x);
            if (isFirst.get()) {
                isFirst.set(Boolean.FALSE);
            }
        });
        return String.format(str, sb);
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }
}
