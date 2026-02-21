package com.plazoleta.usuarios.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmailAlreadyRegisteredExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensajePorDefecto() {
        // Act
        EmailAlreadyRegisteredException exception = new EmailAlreadyRegisteredException();

        // Assert
        assertNotNull(exception);
        assertEquals("A user with this email already exists", exception.getMessage());
    }

    @Test
    void deberiaCrearExcepcionConMensajePersonalizado() {
        // Arrange
        String mensajePersonalizado = "El correo ya está registrado en el sistema";

        // Act
        EmailAlreadyRegisteredException exception = new EmailAlreadyRegisteredException(mensajePersonalizado);

        // Assert
        assertNotNull(exception);
        assertEquals(mensajePersonalizado, exception.getMessage());
    }
}
