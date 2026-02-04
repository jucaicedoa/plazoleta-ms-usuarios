package com.plazoleta.usuarios.domain.exception;

import lombok.Getter;

@Getter
public class ValorExcedeLongitudException extends RuntimeException {

    private final String campo;

    public ValorExcedeLongitudException(String mensaje, String campo) {
        super(mensaje);
        this.campo = campo != null ? campo : "desconocido";
    }
}