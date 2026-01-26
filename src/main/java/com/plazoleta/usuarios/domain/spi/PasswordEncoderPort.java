package com.plazoleta.usuarios.domain.spi;

public interface PasswordEncoderPort {
    String encode(String password);
}