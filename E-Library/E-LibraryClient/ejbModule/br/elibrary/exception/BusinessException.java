package br.elibrary.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BusinessException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;

	public BusinessException(String message) {
        super(message);
    }
}
