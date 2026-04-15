package com.desafio.empleados.service;

import com.desafio.empleados.exception.PersistenciaEmpleadosException;
import com.desafio.empleados.model.Empleado;
import com.desafio.empleados.model.ErrorRegistro;
import com.desafio.empleados.repository.EmpleadoRepository;
import com.desafio.empleados.service.EmpleadoService.ResultadoAlta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmpleadoService")
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    private Empleado baseValido() {
        Empleado e = new Empleado();
        e.setNombre("Ana");
        e.setApellido("Lopez");
        e.setRutDni("11.111.111-1");
        e.setCargo("Dev");
        e.setSalarioBase(new BigDecimal("500000"));
        e.setBono(new BigDecimal("100000"));
        e.setDescuentos(new BigDecimal("50000"));
        return e;
    }

    @Nested
    @DisplayName("validarReglas")
    class ValidarReglas {

        @Test
        void empleadoValidoSinErrores() {
            List<ErrorRegistro> errores = empleadoService.validarReglas(baseValido());
            assertThat(errores).isEmpty();
        }

        @Test
        void nombreVacioGeneraError() {
            Empleado e = baseValido();
            e.setNombre("  ");
            assertThat(codigosCampo(empleadoService.validarReglas(e))).contains("nombre");
        }

        @Test
        void salarioMenor400MilGeneraError() {
            Empleado e = baseValido();
            e.setSalarioBase(new BigDecimal("399999"));
            assertThat(codigosCampo(empleadoService.validarReglas(e))).contains("salarioBase");
        }

        @Test
        void salarioNegativoGeneraError() {
            Empleado e = baseValido();
            e.setSalarioBase(new BigDecimal("-1"));
            assertThat(codigosCampo(empleadoService.validarReglas(e))).contains("salarioBase");
        }

        @Test
        void bonoNegativoGeneraError() {
            Empleado e = baseValido();
            e.setBono(new BigDecimal("-1"));
            assertThat(codigosCampo(empleadoService.validarReglas(e))).contains("bono");
        }

        @Test
        void descuentosNegativosGeneranError() {
            Empleado e = baseValido();
            e.setDescuentos(new BigDecimal("-100"));
            assertThat(codigosCampo(empleadoService.validarReglas(e))).contains("descuentos");
        }

        @Test
        void bonoMayor50PorCientoGeneraError() {
            Empleado e = baseValido();
            e.setBono(new BigDecimal("250001"));
            assertThat(codigosCampo(empleadoService.validarReglas(e))).contains("bono");
        }

        @Test
        void descuentosMayoresASalarioGeneranError() {
            Empleado e = baseValido();
            e.setDescuentos(new BigDecimal("500001"));
            assertThat(codigosCampo(empleadoService.validarReglas(e))).contains("descuentos");
        }

        @Test
        void rutInvalidoGeneraError() {
            Empleado e = baseValido();
            e.setRutDni("1-9");
            assertThat(codigosCampo(empleadoService.validarReglas(e))).contains("rutDni");
        }

        private List<String> codigosCampo(List<ErrorRegistro> errores) {
            return errores.stream().map(ErrorRegistro::getCampo).collect(Collectors.toList());
        }
    }

    @Nested
    @DisplayName("validarYGuardar")
    class Guardar {

        @Test
        void persisteConRutNormalizado() throws SQLException {
            when(empleadoRepository.existsByRutDni(anyString())).thenReturn(false);
            when(empleadoRepository.insert(any(Empleado.class))).thenAnswer(inv -> {
                Empleado arg = inv.getArgument(0);
                Empleado persistido = new Empleado();
                persistido.setId(99L);
                persistido.setNombre(arg.getNombre());
                persistido.setApellido(arg.getApellido());
                persistido.setRutDni(arg.getRutDni());
                persistido.setCargo(arg.getCargo());
                persistido.setSalarioBase(arg.getSalarioBase());
                persistido.setBono(arg.getBono());
                persistido.setDescuentos(arg.getDescuentos());
                return persistido;
            });

            Empleado e = baseValido();
            e.setRutDni("11.111.111-1");

            ResultadoAlta resultado = empleadoService.validarYGuardar(e);
            assertThat(resultado.tieneErrores()).isFalse();
            assertThat(resultado.getEmpleado().getId()).isEqualTo(99L);

            ArgumentCaptor<Empleado> cap = ArgumentCaptor.forClass(Empleado.class);
            verify(empleadoRepository).insert(cap.capture());
            assertThat(cap.getValue().getRutDni()).isEqualTo("11111111-1");
            // El argumento original no debe ser mutado
            assertThat(e.getId()).isNull();
        }

        @Test
        void noInsertaSiValidacionFalla() throws SQLException {
            Empleado e = baseValido();
            e.setSalarioBase(new BigDecimal("100"));

            ResultadoAlta resultado = empleadoService.validarYGuardar(e);
            assertThat(resultado.tieneErrores()).isTrue();
            verify(empleadoRepository, never()).insert(any());
        }

        @Test
        void rutDuplicadoEnRepoDevuelve400() throws SQLException {
            when(empleadoRepository.existsByRutDni("11111111-1")).thenReturn(true);

            ResultadoAlta resultado = empleadoService.validarYGuardar(baseValido());
            assertThat(resultado.tieneErrores()).isTrue();
            assertThat(resultado.getErrores().getErrores()).extracting(ErrorRegistro::getCampo).contains("rutDni");
            verify(empleadoRepository, never()).insert(any());
        }

        @Test
        void sqlState23505SeTraduceADuplicado() throws SQLException {
            SQLException sqlEx = new SQLException("unique", "23505");
            when(empleadoRepository.existsByRutDni(anyString())).thenReturn(false);
            when(empleadoRepository.insert(any(Empleado.class))).thenThrow(sqlEx);

            ResultadoAlta resultado = empleadoService.validarYGuardar(baseValido());
            assertThat(resultado.tieneErrores()).isTrue();
            assertThat(resultado.getErrores().getErrores().get(0).getCampo()).isEqualTo("rutDni");
        }

        @Test
        void otraSQLExceptionPropagaPersistencia() throws SQLException {
            when(empleadoRepository.existsByRutDni(anyString())).thenReturn(false);
            when(empleadoRepository.insert(any(Empleado.class))).thenThrow(new SQLException("fallo", "08000"));

            assertThatThrownBy(() -> empleadoService.validarYGuardar(baseValido()))
                    .isInstanceOf(PersistenciaEmpleadosException.class)
                    .hasMessageContaining("persistir");
        }
    }

    @Nested
    @DisplayName("listar / eliminar")
    class ListarEliminar {

        @Test
        void listarDelegaYPropagaSQLExceptionComoPersistencia() throws SQLException {
            when(empleadoRepository.findAll()).thenThrow(new SQLException("db off"));

            assertThatThrownBy(() -> empleadoService.listar())
                    .isInstanceOf(PersistenciaEmpleadosException.class)
                    .hasMessageContaining("acceder a datos");
        }

        @Test
        void eliminarDelega() throws SQLException {
            when(empleadoRepository.deleteById(5L)).thenReturn(true);
            assertThat(empleadoService.eliminar(5L)).isTrue();
        }

        @Test
        void eliminarSQLExceptionPropagaPersistencia() throws SQLException {
            when(empleadoRepository.deleteById(3L)).thenThrow(new SQLException("x"));
            assertThatThrownBy(() -> empleadoService.eliminar(3L))
                    .isInstanceOf(PersistenciaEmpleadosException.class)
                    .hasMessageContaining("eliminar");
        }
    }
}
