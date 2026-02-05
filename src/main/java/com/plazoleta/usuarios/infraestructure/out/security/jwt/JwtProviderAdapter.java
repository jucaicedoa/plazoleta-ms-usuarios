package com.plazoleta.usuarios.infraestructure.out.security.jwt;

import com.plazoleta.usuarios.domain.model.TokenClaims;
import com.plazoleta.usuarios.domain.spi.JwtProviderPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtProviderAdapter implements JwtProviderPort {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE = "role";

    private final SecretKey secretKey;
    private final long expirationSeconds;

    public JwtProviderAdapter(@Value("${jwt.secret}") String secret,
                              @Value("${jwt.expiration-seconds}") long expirationSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public String generarToken(Integer id, String correo, String rol) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + expirationSeconds * 1000);

        return Jwts.builder()
                .subject(String.valueOf(id))
                .claim(CLAIM_EMAIL, correo)
                .claim(CLAIM_ROLE, rol)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public Optional<TokenClaims> validarToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Integer id = Integer.valueOf(claims.getSubject());
            String correo = claims.get(CLAIM_EMAIL, String.class);
            String rol = claims.get(CLAIM_ROLE, String.class);
            return Optional.of(new TokenClaims(id, correo, rol));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
