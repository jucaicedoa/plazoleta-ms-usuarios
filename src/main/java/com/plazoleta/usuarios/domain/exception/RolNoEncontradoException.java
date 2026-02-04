package com.plazoleta.usuarios.domain.exception;

public class RolNoEncontradoException extends RuntimeException {

    public RolNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}