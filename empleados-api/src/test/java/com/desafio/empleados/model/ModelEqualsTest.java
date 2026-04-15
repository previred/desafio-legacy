package com.desafio.empleados.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Modelos — equals / hashCode")
class ModelEqualsTest {

    @Test
    void empleadoIgualdadPorId() {
        Empleado a = new Empleado();
        a.setId(1L);
        Empleado b = new Empleado();
        b.setId(1L);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
    }

    @Test
    void empleadoDistintoId() {
        Empleado a = new Empleado();
        a.setId(1L);
        Empleado b = new Empleado();
        b.setId(2L);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void empleadoNoIgualAOtroTipo() {
        assertThat(new Empleado()).isNotEqualTo("x");
    }

    @Test
    void errorRegistroEqualsYHashCode() {
        ErrorRegistro a = new ErrorRegistro("campo", "msg");
        ErrorRegistro b = new ErrorRegistro("campo", "msg");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
    }

    @Test
    void errorRegistroConstructorVacioYSetters() {
        ErrorRegistro e = new ErrorRegistro();
        e.setCampo("c");
        e.setMensaje("m");
        assertThat(e.getCampo()).isEqualTo("c");
        assertThat(e.getMensaje()).isEqualTo("m");
    }
}
