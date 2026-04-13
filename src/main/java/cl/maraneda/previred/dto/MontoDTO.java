package cl.maraneda.previred.dto;

import java.io.Serializable;

public class MontoDTO implements Serializable {
    public static final int BONO = 1;
    public static final int DESCUENTO = -1;
    private int id;
    private int monto;
    private int tipo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
