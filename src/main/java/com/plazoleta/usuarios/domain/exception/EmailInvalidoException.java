package com.plazoleta.usuarios.domain.exception;

public class EmailInvalidoException extends RuntimeException {

    public EmailInvalidoException() {
        super("El correo electrónico no tiene un formato válido");
    }
}