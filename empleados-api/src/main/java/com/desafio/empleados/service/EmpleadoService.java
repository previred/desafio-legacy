package com.desafio.empleados.service;

import com.desafio.empleados.exception.PersistenciaEmpleadosException;
import com.desafio.empleados.model.Empleado;
import com.desafio.empleados.model.ErrorRegistro;
import com.desafio.empleados.model.RespuestaErrorValidacion;
import com.desafio.empleados.repository.EmpleadoRepository;
import com.desafio.empleados.validation.RutChilenoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EmpleadoService {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoService.class);

    public static final BigDecimal SALARIO_MINIMO = new BigDecimal("400000");

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional(readOnly = true)
    public List<Empleado> listar() {
        try {
            return empleadoRepository.findAll();
        } catch (SQLException e) {
            log.error("Error al listar empleados", e);
            throw new PersistenciaEmpleadosException("Error al acceder a datos", e);
        }
    }

    /**
     * Valida las reglas de negocio y persiste el empleado.
     *
     * @return resultado con el empleado persistido (con id asignado), o con los errores de validación presentes.
     */
    @Transactional
    public ResultadoAlta validarYGuardar(Empleado empleado) {
        List<ErrorRegistro> errores = validarReglas(empleado);
        if (!errores.isEmpty()) {
            RespuestaErrorValidacion r = new RespuestaErrorValidacion();
            r.setErrores(errores);
            return ResultadoAlta.conErrores(r);
        }
        String rutNorm = RutChilenoValidator.normalizar(empleado.getRutDni());
        Empleado conRutNorm = copiarRut(empleado, rutNorm);
        try {
            if (empleadoRepository.existsByRutDni(rutNorm)) {
                return ResultadoAlta.conErrores(conUnError("rutDni", "Ya existe un empleado con este RUT/DNI."));
            }
            Empleado persistido = empleadoRepository.insert(conRutNorm);
            return ResultadoAlta.exitoso(persistido);
        } catch (SQLException e) {
            if (esViolacionUnicidad(e)) {
                return ResultadoAlta.conErrores(conUnError("rutDni", "Ya existe un empleado con este RUT/DNI."));
            }
            log.error("Error al insertar empleado", e);
            throw new PersistenciaEmpleadosException("Error al persistir empleado", e);
        }
    }

    /** Valor de retorno de {@link #validarYGuardar}: agrupa el empleado persistido o los errores. */
    public static final class ResultadoAlta {
        private final Empleado empleado;
        private final RespuestaErrorValidacion errores;

        private ResultadoAlta(Empleado empleado, RespuestaErrorValidacion errores) {
            this.empleado = empleado;
            this.errores = errores;
        }

        public static ResultadoAlta exitoso(Empleado empleado) {
            return new ResultadoAlta(empleado, null);
        }

        public static ResultadoAlta conErrores(RespuestaErrorValidacion errores) {
            return new ResultadoAlta(null, errores);
        }

        public boolean tieneErrores() {
            return errores != null;
        }

        public Empleado getEmpleado() {
            return empleado;
        }

        public RespuestaErrorValidacion getErrores() {
            return errores;
        }
    }

    private static Empleado copiarRut(Empleado origen, String rutNormalizado) {
        Empleado copia = new Empleado();
        copia.setNombre(origen.getNombre());
        copia.setApellido(origen.getApellido());
        copia.setRutDni(rutNormalizado);
        copia.setCargo(origen.getCargo());
        copia.setSalarioBase(origen.getSalarioBase());
        copia.setBono(origen.getBono());
        copia.setDescuentos(origen.getDescuentos());
        return copia;
    }

    /**
     * Reglas Parte 2 + campos obligatorios + montos no negativos.
     */
    public List<ErrorRegistro> validarReglas(Empleado e) {
        BigDecimal salario = e.getSalarioBase();
        BigDecimal bono = e.getBono() != null ? e.getBono() : BigDecimal.ZERO;
        BigDecimal desc = e.getDescuentos() != null ? e.getDescuentos() : BigDecimal.ZERO;

        List<ErrorRegistro> errores = Stream.of(
                        errorSi(isBlank(e.getNombre()), "nombre", "El nombre es obligatorio."),
                        errorSi(isBlank(e.getApellido()), "apellido", "El apellido es obligatorio."),
                        errorSi(isBlank(e.getRutDni()), "rutDni", "El RUT/DNI es obligatorio."),
                        errorSi(!isBlank(e.getRutDni()) && !RutChilenoValidator.esValido(e.getRutDni()),
                                "rutDni", "El RUT chileno no es válido (formato y dígito verificador)."),
                        errorSi(isBlank(e.getCargo()), "cargo", "El cargo es obligatorio."),
                        errorSalario(salario),
                        errorSi(e.getBono() != null && e.getBono().compareTo(BigDecimal.ZERO) < 0,
                                "bono", "El bono no puede ser negativo."),
                        errorSi(e.getDescuentos() != null && e.getDescuentos().compareTo(BigDecimal.ZERO) < 0,
                                "descuentos", "Los descuentos no pueden ser negativos."))
                .flatMap(opt -> opt.map(Stream::of).orElseGet(Stream::empty))
                .collect(Collectors.toList());

        errores.addAll(reglasBonoDescuento(salario, bono, desc));
        return errores;
    }

    private static List<ErrorRegistro> reglasBonoDescuento(BigDecimal salario, BigDecimal bono, BigDecimal desc) {
        List<ErrorRegistro> r = new ArrayList<>();
        if (salario != null && salario.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal maxBono = salario.multiply(new BigDecimal("0.5")).setScale(0, RoundingMode.HALF_UP);
            if (bono.compareTo(maxBono) > 0) {
                r.add(new ErrorRegistro("bono", "Los bonos no pueden superar el 50% del salario base."));
            }
            if (desc.compareTo(salario) > 0) {
                r.add(new ErrorRegistro("descuentos", "El total de descuentos no puede ser mayor al salario base."));
            }
        }
        return r;
    }

    private static Optional<ErrorRegistro> errorSi(boolean condicion, String campo, String mensaje) {
        if (condicion) {
            return Optional.of(new ErrorRegistro(campo, mensaje));
        }
        return Optional.empty();
    }

    /** Una sola regla por fallo de salario (obligatorio / negativo / mínimo). */
    private static Optional<ErrorRegistro> errorSalario(BigDecimal salario) {
        if (salario == null) {
            return Optional.of(new ErrorRegistro("salarioBase", "El salario base es obligatorio."));
        }
        if (salario.compareTo(BigDecimal.ZERO) < 0) {
            return Optional.of(new ErrorRegistro("salarioBase", "El salario base no puede ser negativo."));
        }
        if (salario.compareTo(SALARIO_MINIMO) < 0) {
            return Optional.of(new ErrorRegistro("salarioBase", "El salario base no puede ser menor a $400.000."));
        }
        return Optional.empty();
    }

    @Transactional
    public boolean eliminar(long id) {
        try {
            return empleadoRepository.deleteById(id);
        } catch (SQLException e) {
            log.error("Error al eliminar empleado id={}", id, e);
            throw new PersistenciaEmpleadosException("Error al eliminar empleado", e);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static RespuestaErrorValidacion conUnError(String campo, String mensaje) {
        RespuestaErrorValidacion r = new RespuestaErrorValidacion();
        List<ErrorRegistro> lista = new ArrayList<>();
        lista.add(new ErrorRegistro(campo, mensaje));
        r.setErrores(lista);
        return r;
    }

    private static boolean esViolacionUnicidad(SQLException e) {
        if ("23505".equals(e.getSQLState())) {
            return true;
        }
        String m = e.getMessage();
        return m != null && m.toUpperCase(Locale.ROOT).contains("UNIQUE");
    }
}
