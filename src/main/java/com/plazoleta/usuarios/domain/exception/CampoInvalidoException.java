package com.plazoleta.usuarios.domain.exception;

public class CampoInvalidoException extends RuntimeException {

    public CampoInvalidoException(String mensaje) {
        super(mensaje);
    }
}