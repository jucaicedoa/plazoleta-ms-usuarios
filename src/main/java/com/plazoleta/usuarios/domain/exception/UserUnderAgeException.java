package com.plazoleta.usuarios.domain.exception;

public class UserUnderAgeException extends RuntimeException {

    public UserUnderAgeException() {
        super("User must be at least 18 years old");
    }
}
