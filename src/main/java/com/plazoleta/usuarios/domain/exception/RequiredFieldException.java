package com.plazoleta.usuarios.domain.exception;

public class RequiredFieldException extends RuntimeException {

    public RequiredFieldException() {
        super("Required field is missing");
    }

    public RequiredFieldException(String message) {
        super(message);
    }
}
