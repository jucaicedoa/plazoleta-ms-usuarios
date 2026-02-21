package com.plazoleta.usuarios.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RequiredFieldExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensajePorDefecto() {
        // Act
        RequiredFieldException exception = new RequiredFieldException();

        // Assert
        assertNotNull(exception);
        assertEquals("Required field is missing", exception.getMessage());
    }

    @Test
    void deberiaCrearExcepcionConMensajePersonalizado() {
        // Arrange
        String mensajePersonalizado = "El campo nombre es obligatorio";

        // Act
        RequiredFieldException exception = new RequiredFieldException(mensajePersonalizado);

        // Assert
        assertNotNull(exception);
        assertEquals(mensajePersonalizado, exception.getMessage());
    }
}
