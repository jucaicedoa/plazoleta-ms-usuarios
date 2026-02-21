package com.plazoleta.usuarios.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DocumentAlreadyRegisteredExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensajePorDefecto() {
        // Act
        DocumentAlreadyRegisteredException exception = new DocumentAlreadyRegisteredException();

        // Assert
        assertNotNull(exception);
        assertEquals("A user with this document number already exists", exception.getMessage());
    }

    @Test
    void deberiaCrearExcepcionConMensajePersonalizado() {
        // Arrange
        String mensajePersonalizado = "El documento ya está registrado";

        // Act
        DocumentAlreadyRegisteredException exception = new DocumentAlreadyRegisteredException(mensajePersonalizado);

        // Assert
        assertNotNull(exception);
        assertEquals(mensajePersonalizado, exception.getMessage());
    }
}
