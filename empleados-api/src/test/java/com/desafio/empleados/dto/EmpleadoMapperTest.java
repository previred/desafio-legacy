package com.desafio.empleados.dto;

import com.desafio.empleados.model.Empleado;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class EmpleadoMapperTest {

    @Test
    void mapeaTodosLosCamposANuevaEntidad() {
        EmpleadoAltaRequest req = new EmpleadoAltaRequest();
        req.setNombre("N");
        req.setApellido("A");
        req.setRutDni("1-9");
        req.setCargo("C");
        req.setSalarioBase(new BigDecimal("500000"));
        req.setBono(new BigDecimal("1"));
        req.setDescuentos(new BigDecimal("2"));

        Empleado e = EmpleadoMapper.toNuevaEntidad(req);
        assertThat(e.getId()).isNull();
        assertThat(e.getNombre()).isEqualTo("N");
        assertThat(e.getApellido()).isEqualTo("A");
        assertThat(e.getRutDni()).isEqualTo("1-9");
        assertThat(e.getCargo()).isEqualTo("C");
        assertThat(e.getSalarioBase()).isEqualByComparingTo("500000");
        assertThat(e.getBono()).isEqualByComparingTo("1");
        assertThat(e.getDescuentos()).isEqualByComparingTo("2");
    }

    @Test
    void toResponseCopiaCampos() {
        Empleado e = new Empleado();
        e.setId(7L);
        e.setNombre("N");
        e.setApellido("A");
        e.setRutDni("11111111-1");
        e.setCargo("C");
        e.setSalarioBase(new BigDecimal("500000"));
        e.setBono(BigDecimal.ZERO);
        e.setDescuentos(BigDecimal.ZERO);
        EmpleadoResponse r = EmpleadoMapper.toResponse(e);
        assertThat(r.getId()).isEqualTo(7L);
        assertThat(r.getNombre()).isEqualTo("N");
        assertThat(r.getRutDni()).isEqualTo("11111111-1");
        assertThat(r.getSalarioBase()).isEqualByComparingTo("500000");
    }
}
