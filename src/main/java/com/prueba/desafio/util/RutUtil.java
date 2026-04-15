package com.prueba.desafio.util;
import org.springframework.stereotype.Component;

@Component
public class RutUtil {
    public String generarRutFormateado(int rutNumero) {
        char dv = calcularDV(rutNumero);

        String rutStr = String.valueOf(rutNumero);
        StringBuilder rutConPuntos = new StringBuilder();

        int contador = 0;

        for (int i = rutStr.length() - 1; i >= 0; i--) {
            rutConPuntos.insert(0, rutStr.charAt(i));
            contador++;

            if (contador == 3 && i != 0) {
                rutConPuntos.insert(0, ".");
                contador = 0;
            }
        }

        return rutConPuntos.toString() + "-" + dv;
    }
    public char calcularDV(int rut) {
        int suma = 0;
        int multiplicador = 2;

        while (rut > 0) {
            int digito = rut % 10;
            suma += digito * multiplicador;

            rut /= 10;
            multiplicador = (multiplicador == 7) ? 2 : multiplicador + 1;
        }

        int resto = 11 - (suma % 11);

        if (resto == 11) return '0';
        if (resto == 10) return 'K';

        return (char) (resto + '0');
    }
    public boolean esRutDniValido(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }

        rut = rut.replace(".", "").replace("-", "").toUpperCase();

        if (rut.length() < 8) {
            return false;
        }

        String cuerpo = rut.substring(0, rut.length() - 1);
        char dvIngresado = rut.charAt(rut.length() - 1);

        try {
            int rutNumero = Integer.parseInt(cuerpo);

            char dvCalculado = calcularDV(rutNumero);

            return dvCalculado == dvIngresado;

        } catch (NumberFormatException e) {
            return false;
        }
    }
}
