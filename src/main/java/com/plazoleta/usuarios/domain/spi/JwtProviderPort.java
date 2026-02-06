package com.plazoleta.usuarios.domain.spi;

import com.plazoleta.usuarios.domain.model.TokenClaims;

import java.util.Optional;

public interface JwtProviderPort {

    String generarToken(Integer id, String correo, String rol);

    /**
     * Genera el token incluyendo restauranteId (propietarios que crean empleados).
     */
    String generarToken(Integer id, String correo, String rol, Integer restauranteId);

    Optional<TokenClaims> validarToken(String token);
}