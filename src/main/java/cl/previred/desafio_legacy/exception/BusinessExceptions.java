package cl.previred.desafio_legacy.exception;

import com.fasterxml.jackson.annotation.JsonValue;


/**
 * 
 * Exception de negocio para retornar respuesta controladas.
 * 
 * @author Christopher Gaete Oliveres.
 * @version 1.0.0, 02/05/2026
 * 
 */
public class BusinessExceptions extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private ErrorCod errorCod;
	
	
	public BusinessExceptions(ErrorCod errorCod, String message) {
		super(message);
		this.errorCod = errorCod;
	}

	public BusinessExceptions(ErrorCod errorCod, String message,Throwable cause) {
		super(message, cause);
		this.errorCod = errorCod;
	}
	
	

	public ErrorCod getErrorCod() {
		return errorCod;
	}

	@JsonValue
	public void setErrorCod(ErrorCod errorCod) {
		this.errorCod = errorCod;
	}

}
