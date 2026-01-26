package com.plazoleta.usuarios.domain.exception;

public class UsuarioMayorDeEdadException extends RuntimeException {

    public UsuarioMayorDeEdadException() {
        super("El usuario debe ser mayor de edad");
    }
}