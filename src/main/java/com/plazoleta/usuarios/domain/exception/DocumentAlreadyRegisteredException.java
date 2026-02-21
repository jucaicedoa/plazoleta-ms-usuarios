package com.plazoleta.usuarios.domain.exception;

public class DocumentAlreadyRegisteredException extends RuntimeException {

    public DocumentAlreadyRegisteredException() {
        super("A user with this document number already exists");
    }

    public DocumentAlreadyRegisteredException(String message) {
        super(message);
    }
}
