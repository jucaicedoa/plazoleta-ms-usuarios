package com.plazoleta.usuarios.infraestructure.configuration;

import com.plazoleta.usuarios.domain.api.UserServicePort;
import com.plazoleta.usuarios.domain.spi.JwtProviderPort;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UserPersistencePort;
import com.plazoleta.usuarios.infraestructure.out.jpa.mapper.UserEntityMapper;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.RoleRepository;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.UserRepository;
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
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserEntityMapper userEntityMapper;

    @Mock
    private JwtProviderPort jwtProviderPort;

    private BeanConfiguration beanConfiguration;

    @BeforeEach
    void setUp() {
        beanConfiguration = new BeanConfiguration(userRepository, roleRepository, userEntityMapper, jwtProviderPort);
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
    void shouldCreateUserServicePortBean() {
        UserServicePort userServicePort = beanConfiguration.userServicePort();

        assertNotNull(userServicePort);
        assertTrue(userServicePort instanceof UserServicePort);
    }

    @Test
    void shouldCreateUserPersistencePortBean() {
        UserPersistencePort userPersistencePort = beanConfiguration.userPersistencePort();

        assertNotNull(userPersistencePort);
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
    void shouldInjectDependenciesCorrectly() {
        UserServicePort servicePort = beanConfiguration.userServicePort();

        assertNotNull(servicePort);
    }
}