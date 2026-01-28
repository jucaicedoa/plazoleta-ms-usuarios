package com.plazoleta.usuarios.infraestructure.configuration;

import com.plazoleta.usuarios.domain.api.UsuarioServicePort;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class BeanConfigurationTest {

    @Mock
    private UsuarioPersistencePort usuarioPersistencePort;

    private BeanConfiguration beanConfiguration;

    @BeforeEach
    void setUp() {
        beanConfiguration = new BeanConfiguration(usuarioPersistencePort);
    }

    @Test
    void deberiaCrearPasswordEncoderPortBean() {
        // Act
        PasswordEncoderPort passwordEncoderPort = beanConfiguration.passwordEncoderPort();

        // Assert
        assertNotNull(passwordEncoderPort);

        // Verificar que encripta correctamente
        String passwordEncriptada = passwordEncoderPort.encode("password123");
        assertNotNull(passwordEncriptada);
        assertNotEquals("password123", passwordEncriptada);
        assertTrue(passwordEncriptada.length() > 20); // BCrypt genera hashes largos
    }

    @Test
    void deberiaCrearUsuarioServicePortBean() {
        // Act
        UsuarioServicePort usuarioServicePort = beanConfiguration.usuarioServicePort();

        // Assert
        assertNotNull(usuarioServicePort);
        // Verificar que es una instancia de UsuarioServicePort (puede ser implementación anónima)
        assertTrue(usuarioServicePort instanceof UsuarioServicePort);
    }

    @Test
    void passwordEncoderDeberiaGenerarHashesDiferentesParaMismaPassword() {
        // Arrange
        PasswordEncoderPort passwordEncoderPort = beanConfiguration.passwordEncoderPort();
        String password = "password123";

        // Act
        String hash1 = passwordEncoderPort.encode(password);
        String hash2 = passwordEncoderPort.encode(password);

        // Assert
        assertNotEquals(hash1, hash2); // BCrypt genera diferentes salts
    }

    @Test
    void deberiaInyectarDependenciasCorrectamente() {
        // Act
        UsuarioServicePort servicePort = beanConfiguration.usuarioServicePort();

        // Assert
        assertNotNull(servicePort);
        // El bean debe estar correctamente configurado con las dependencias
    }
}