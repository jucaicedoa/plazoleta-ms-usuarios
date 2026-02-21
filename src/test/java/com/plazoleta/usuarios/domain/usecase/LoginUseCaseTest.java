package com.plazoleta.usuarios.domain.usecase;

import com.plazoleta.usuarios.domain.exception.InvalidCredentialsException;
import com.plazoleta.usuarios.domain.model.Role;
import com.plazoleta.usuarios.domain.model.User;
import com.plazoleta.usuarios.domain.spi.JwtProviderPort;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private JwtProviderPort jwtProviderPort;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(userPersistencePort, passwordEncoderPort, jwtProviderPort);
    }

    @Test
    void shouldReturnTokenWhenCredentialsAreCorrect() {
        String email = "usuario@mail.com";
        String password = "clave123";
        User user = createUserWithRole(email, "encodedPassword", "PROPIETARIO");
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

        when(userPersistencePort.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(password, user.getPassword())).thenReturn(true);
        when(jwtProviderPort.generateToken(user.getId(), user.getEmail(), "PROPIETARIO", null)).thenReturn(expectedToken);

        String token = loginUseCase.login(email, password, null);

        assertEquals(expectedToken, token);
        verify(userPersistencePort).findByEmail(email);
        verify(passwordEncoderPort).matches(password, user.getPassword());
        verify(jwtProviderPort).generateToken(user.getId(), user.getEmail(), "PROPIETARIO", null);
    }

    @Test
    void shouldThrowInvalidCredentialsWhenUserDoesNotExist() {
        String email = "noexiste@mail.com";
        String password = "clave123";

        when(userPersistencePort.findByEmail(email)).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> loginUseCase.login(email, password, null)
        );
        assertEquals("Invalid credentials", exception.getMessage());
        verify(userPersistencePort).findByEmail(email);
        verify(passwordEncoderPort, org.mockito.Mockito.never()).matches(anyString(), anyString());
        verify(jwtProviderPort, org.mockito.Mockito.never()).generateToken(anyInt(), anyString(), anyString(), any());
    }

    @Test
    void shouldThrowInvalidCredentialsWhenPasswordIsWrong() {
        String email = "usuario@mail.com";
        String password = "wrongPassword";
        User user = createUserWithRole(email, "encodedPassword", "PROPIETARIO");

        when(userPersistencePort.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(password, user.getPassword())).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> loginUseCase.login(email, password, null)
        );
        assertEquals("Invalid credentials", exception.getMessage());
        verify(userPersistencePort).findByEmail(email);
        verify(passwordEncoderPort).matches(password, user.getPassword());
        verify(jwtProviderPort, org.mockito.Mockito.never()).generateToken(anyInt(), anyString(), anyString(), any());
    }

    private User createUserWithRole(String email, String encodedPassword, String roleName) {
        User user = new User();
        user.setId(1);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(new Role(null, roleName));
        return user;
    }
}