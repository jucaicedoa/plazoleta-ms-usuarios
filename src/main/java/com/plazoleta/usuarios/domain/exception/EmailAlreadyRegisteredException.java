package com.plazoleta.usuarios.domain.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException() {
        super("A user with this email already exists");
    }

    public EmailAlreadyRegisteredException(String message) {
        super(message);
    }
}
