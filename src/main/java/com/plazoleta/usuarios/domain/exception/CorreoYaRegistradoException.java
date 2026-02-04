package com.plazoleta.usuarios.domain.exception;

public class CorreoYaRegistradoException extends RuntimeException {

    public CorreoYaRegistradoException() {
        super("Ya existe un usuario con este correo electrónico");
    }

    public CorreoYaRegistradoException(String mensaje) {
        super(mensaje);
    }
}