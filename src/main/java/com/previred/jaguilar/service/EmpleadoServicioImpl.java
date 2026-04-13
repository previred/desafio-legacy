package com.previred.jaguilar.service;

import com.previred.jaguilar.dao.EmpleadoDao;
import com.previred.jaguilar.dao.EmpleadoDaoImpl;
import com.previred.jaguilar.model.Empleado;
import com.previred.jaguilar.model.ErrorValidacion;
import com.previred.jaguilar.util.LogUtil;
import com.previred.jaguilar.util.RutUtil;

import java.math.BigDecimal;
import java.util.List;

public class EmpleadoServicioImpl implements EmpleadoServicio {

    private static final String CAMPO_EMPLEADO = "empleado";
    private static final String CAMPO_NOMBRE = "nombre";
    private static final String CAMPO_APELLIDO = "apellido";
    private static final String CAMPO_RUT_DNI = "rutDni";
    private static final String CAMPO_CARGO = "cargo";
    private static final String CAMPO_SALARIO = "salario";
    private static final String CAMPO_BONO = "bono";
    private static final String CAMPO_DESCUENTOS = "descuentos";
    private static final String CAMPO_ID = "id";

    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("400000");
    private static final BigDecimal PORCENTAJE_MAXIMO_BONO = new BigDecimal("0.50");

    private final EmpleadoDao empleadoDao;

    public EmpleadoServicioImpl() {
        this.empleadoDao = new EmpleadoDaoImpl();
    }

    @Override
    public List<Empleado> listarEmpleados() {
        LogUtil.info("SERVICE: Listando empleados.");
        return empleadoDao.listarTodos();
    }

    @Override
    public void registrarEmpleado(Empleado empleado, List<ErrorValidacion> errores) {
        if (empleado == null) {
            errores.add(new ErrorValidacion(CAMPO_EMPLEADO, "Los datos del empleado son obligatorios."));
            LogUtil.warning("SERVICE: Se intento registrar un empleado nulo.");
            return;
        }

        empleado.setRutDni(RutUtil.normalizar(empleado.getRutDni()));

        validarEmpleado(empleado, errores);

        if (!errores.isEmpty()) {
            LogUtil.warning("SERVICE: Error de validación al registrar empleado.");
            return;
        }

        if (empleadoDao.existePorRutDni(empleado.getRutDni())) {
            errores.add(new ErrorValidacion(CAMPO_RUT_DNI, "Ya existe un empleado con ese RUT/DNI."));
            LogUtil.warning("SERVICE: Ya existe un empleado con rutDni=" + empleado.getRutDni());
            return;
        }

        empleadoDao.guardar(empleado);
        LogUtil.info("SERVICE: Empleado registrado correctamente con id=" + empleado.getId());
    }

    @Override
    public void eliminarEmpleado(Long id, List<ErrorValidacion> errores) {
        if (id == null || id <= 0) {
            errores.add(new ErrorValidacion(CAMPO_ID, "El id es obligatorio y debe ser válido."));
            LogUtil.warning("SERVICE: Se intentó eliminar con id inválido.");
            return;
        }

        boolean eliminado = empleadoDao.eliminarPorId(id);
        if (!eliminado) {
            errores.add(new ErrorValidacion(CAMPO_ID, "No se encontró un empleado con ese id."));
            LogUtil.warning("SERVICE: No se encontró empleado para eliminar con id=" + id);
            return;
        }

        LogUtil.info("SERVICE: Empleado eliminado correctamente con id=" + id);
    }

    private void validarEmpleado(Empleado empleado, List<ErrorValidacion> errores) {
        if (textoVacio(empleado.getNombre())) {
            errores.add(new ErrorValidacion(CAMPO_NOMBRE, "El nombre es obligatorio."));
        }

        if (textoVacio(empleado.getApellido())) {
            errores.add(new ErrorValidacion(CAMPO_APELLIDO, "El apellido es obligatorio."));
        }

        if (textoVacio(empleado.getRutDni())) {
            errores.add(new ErrorValidacion(CAMPO_RUT_DNI, "El RUT/DNI es obligatorio."));
        } else if (!RutUtil.formatoValido(empleado.getRutDni())) {
            errores.add(new ErrorValidacion(CAMPO_RUT_DNI, "El formato del RUT/DNI no es válido."));
        }

        if (textoVacio(empleado.getCargo())) {
            errores.add(new ErrorValidacion(CAMPO_CARGO, "El cargo es obligatorio."));
        }

        if (empleado.getSalario() == null) {
            errores.add(new ErrorValidacion(CAMPO_SALARIO, "El salario es obligatorio."));
        } else if (empleado.getSalario().compareTo(SALARIO_MINIMO) < 0) {
            errores.add(new ErrorValidacion(CAMPO_SALARIO, "El salario no puede ser menor a 400000."));
        }

        if (empleado.getBono() != null && empleado.getSalario() != null) {
            BigDecimal limiteBono = empleado.getSalario().multiply(PORCENTAJE_MAXIMO_BONO);
            if (empleado.getBono().compareTo(limiteBono) > 0) {
                errores.add(new ErrorValidacion(CAMPO_BONO, "El bono no puede superar el 50% del salario base."));
            }
        }

        if (empleado.getDescuentos() != null
                && empleado.getSalario() != null
                && empleado.getDescuentos().compareTo(empleado.getSalario()) > 0) {
            errores.add(new ErrorValidacion(CAMPO_DESCUENTOS, "Los descuentos no pueden ser mayores al salario base."));
        }
    }

    private boolean textoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}