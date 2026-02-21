package com.plazoleta.usuarios.domain.exception;

public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException() {
        super("Email format is invalid");
    }
}
