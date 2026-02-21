package com.plazoleta.usuarios.domain.spi;

import com.plazoleta.usuarios.domain.model.TokenClaims;

import java.util.Optional;

public interface JwtProviderPort {

    String generateToken(Integer id, String email, String role);

    String generateToken(Integer id, String email, String role, Integer restaurantId);

    Optional<TokenClaims> validateToken(String token);
}
