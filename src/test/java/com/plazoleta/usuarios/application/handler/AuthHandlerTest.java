package com.plazoleta.usuarios.application.handler;

import com.plazoleta.usuarios.application.dto.LoginDto;
import com.plazoleta.usuarios.domain.api.AuthServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {

    @Mock
    private AuthServicePort authServicePort;

    private AuthHandler handler;

    @BeforeEach
    void setUp() {
        handler = new AuthHandler(authServicePort);
    }

    @Test
    void shouldCallAuthServicePortAndReturnToken() {
        LoginDto dto = new LoginDto("user@example.com", "password123", 1);
        String expectedToken = "jwt-token-abc";

        when(authServicePort.login("user@example.com", "password123", 1))
                .thenReturn(expectedToken);

        String result = handler.login(dto);

        assertEquals(expectedToken, result);
        verify(authServicePort, times(1)).login("user@example.com", "password123", 1);
    }

    @Test
    void shouldCallAuthServicePortWithNullRestaurantId() {
        LoginDto dto = new LoginDto("owner@example.com", "pass456", null);
        String expectedToken = "token-owner";

        when(authServicePort.login("owner@example.com", "pass456", null))
                .thenReturn(expectedToken);

        String result = handler.login(dto);

        assertEquals(expectedToken, result);
        verify(authServicePort, times(1)).login("owner@example.com", "pass456", null);
    }

    @Test
    void shouldPropagateExceptionFromAuthServicePort() {
        LoginDto dto = new LoginDto("bad@example.com", "wrong", 1);

        when(authServicePort.login(anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(RuntimeException.class, () -> handler.login(dto));
        verify(authServicePort, times(1)).login("bad@example.com", "wrong", 1);
    }
}
