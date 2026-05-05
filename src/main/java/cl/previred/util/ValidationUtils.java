package cl.previred.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cl.previred.dto.EmpleadoRequest;

public class ValidationUtils {

    private static final Pattern RUT_PATTERN = Pattern.compile("^[0-9]{1,2}\\.?[0-9]{3}\\.?[0-9]{3}-[0-9kK]{1}$");
    private static final int MIN_SALARIO_BASE = 400000;

    private ValidationUtils() {
    }
    
    /*
    1. En el backend (Java 8): 
        - 
        - 
        - Bonos no pueden superar el 50% del salario base. 
        - El total de descuentos no puede ser mayor al salario base. 
        - Si alguna regla se incumple, se debe retornar una respuesta HTTP 400 con un JSON indicando los registros con error.
        
        */

    public static List<String> validateEmpleadoRequest(EmpleadoRequest request) {
        List<String> errors = new ArrayList<String>();
        if (isBlank(request.getNombre())) errors.add("El nombre es obligatorio");
        if (isBlank(request.getApellido())) errors.add("El apellido es obligatorio");
        if (isBlank(request.getRutDni())) errors.add("El RUT/DNI es obligatorio");
        else if (!isValidRutOrDni(request.getRutDni())) errors.add("El RUT/DNI no tiene un formato válido");
        if (isBlank(request.getCargo())) errors.add("El cargo es obligatorio");
        if (request.getSalario() == 0) errors.add("El salario es obligatorio");
        else if (request.getSalario() < MIN_SALARIO_BASE)  errors.add("El salario base no puede ser menor a 400000");
        
        errors = validarSalario(request.getSalario(), errors);
        
        return errors;
    }
    
    
    private static List<String> validarSalario(int salario, List<String> errors) {
		// TODO Auto-generated method stub
    	

    	int max_bono = (int) ( MIN_SALARIO_BASE * 0.5);
        int max_descuento = MIN_SALARIO_BASE;
        int descuentos = (int) (salario * 0.2);
        int bono = (int) (salario * 0.25);
    
        if (bono > max_bono) errors.add("Bonos no pueden superar el 50% del salario base");
        if (descuentos > max_descuento) errors.add("El total de descuentos no puede ser mayor al salario base");
		return errors;
	}

    

    public static boolean isValidRutOrDni(String value) {
        if (value == null) return false;
        String trimmed = value.trim();
        return RUT_PATTERN.matcher(trimmed).matches();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
