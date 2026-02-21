package com.plazoleta.usuarios.domain.exception;

import lombok.Getter;

@Getter
public class ValueExceedsLengthException extends RuntimeException {

    private final String field;

    public ValueExceedsLengthException(String message, String field) {
        super(message);
        this.field = field != null ? field : "unknown";
    }
}
