package com.plazoleta.usuarios.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CorreoYaRegistradoExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensajePorDefecto() {
        // Act
        CorreoYaRegistradoException exception = new CorreoYaRegistradoException();

        // Assert
        assertNotNull(exception);
        assertEquals("Ya existe un usuario con este correo electrónico", exception.getMessage());
    }

    @Test
    void deberiaCrearExcepcionConMensajePersonalizado() {
        // Arrange
        String mensajePersonalizado = "El correo ya está registrado en el sistema";

        // Act
        CorreoYaRegistradoException exception = new CorreoYaRegistradoException(mensajePersonalizado);

        // Assert
        assertNotNull(exception);
        assertEquals(mensajePersonalizado, exception.getMessage());
    }
}
