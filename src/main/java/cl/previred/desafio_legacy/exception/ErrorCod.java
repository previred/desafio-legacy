package cl.previred.desafio_legacy.exception;

import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * 
 * Enum para respuesta con error.
 * 
 * @author Christopher Gaete Oliveres.
 * @version 1.0.0, 01/05/2026
 * 
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCod {

	DUPLICATE_RECORD(100, "El registro ya existe en base datos."),
	SALARY(200, "El salario no puede ser inferior a $400.000"),
	BONUS(300, "El bono no puede supera al 50% del salario base"),
	DEDUCTION(400, "EL total de descuento no puede ser superor al salario base"),
	GENERIC_ERROR(999, "Ocurrio un error.");

	private final int codigo;
	private final String messages;

	public int getCodigo() {
		return codigo;
	}

	public String getMessages() {
		return messages;
	}

	ErrorCod(int codigo, String messages) {
		this.codigo = codigo;
		this.messages = messages;
	}
	
	
}
