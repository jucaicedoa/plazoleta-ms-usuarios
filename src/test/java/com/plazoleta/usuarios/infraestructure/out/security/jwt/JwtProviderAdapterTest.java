package com.plazoleta.usuarios.infraestructure.out.security.jwt;

import com.plazoleta.usuarios.domain.model.TokenClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtProviderAdapterTest {

    private static final String SECRET = "12345678901234567890123456789012";
    private static final long EXPIRATION_SECONDS = 3600L;

    private JwtProviderAdapter jwtProviderAdapter;

    @BeforeEach
    void setUp() {
        jwtProviderAdapter = new JwtProviderAdapter(SECRET, EXPIRATION_SECONDS);
    }

    @Test
    void deberiaGenerarTokenConDatosDelUsuario() {
        // Arrange
        Integer id = 1;
        String correo = "usuario@mail.com";
        String rol = "PROPIETARIO";

        // Act
        String token = jwtProviderAdapter.generateToken(id, correo, rol);

        // Assert
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT tiene 3 partes
    }

    @Test
    void deberiaValidarTokenYRetornarClaimsCorrectos() {
        // Arrange
        Integer id = 2;
        String correo = "propietario@mail.com";
        String rol = "PROPIETARIO";
        String token = jwtProviderAdapter.generateToken(id, correo, rol);

        // Act
        Optional<TokenClaims> claimsOpt = jwtProviderAdapter.validateToken(token);

        // Assert
        assertTrue(claimsOpt.isPresent());
        TokenClaims claims = claimsOpt.get();
        assertEquals(id, claims.getId());
        assertEquals(correo, claims.getEmail());
        assertEquals(rol, claims.getRole());
    }

    @Test
    void deberiaRetornarEmptyCuandoTokenEsInvalido() {
        // Arrange
        String tokenInvalido = "token.invalido.malformado";

        // Act
        Optional<TokenClaims> claimsOpt = jwtProviderAdapter.validateToken(tokenInvalido);

        // Assert
        assertFalse(claimsOpt.isPresent());
    }

    @Test
    void deberiaRetornarEmptyCuandoTokenEsModificado() {
        // Arrange
        String token = jwtProviderAdapter.generateToken(1, "a@b.com", "ADMINISTRADOR");
        String tokenModificado = token.substring(0, token.length() - 2) + "XX";

        // Act
        Optional<TokenClaims> claimsOpt = jwtProviderAdapter.validateToken(tokenModificado);

        // Assert
        assertFalse(claimsOpt.isPresent());
    }

    @Test
    void deberiaRetornarEmptyCuandoTokenTieneOtroSecret() {
        // Arrange: otro adapter con distinto secret
        JwtProviderAdapter otroAdapter = new JwtProviderAdapter("otro_secret_12345678901234567890", EXPIRATION_SECONDS);
        String tokenDeOtro = otroAdapter.generateToken(1, "a@b.com", "PROPIETARIO");

        // Act: validate with our adapter (different secret)
        Optional<TokenClaims> claimsOpt = jwtProviderAdapter.validateToken(tokenDeOtro);

        // Assert
        assertFalse(claimsOpt.isPresent());
    }
}