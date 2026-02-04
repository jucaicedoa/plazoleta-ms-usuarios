package com.plazoleta.usuarios.domain.exception;

public class CampoObligatorioException extends RuntimeException {

    public CampoObligatorioException() {
        super("Falta un campo obligatorio");
    }

    public CampoObligatorioException(String mensaje) {
        super(mensaje);
    }
}