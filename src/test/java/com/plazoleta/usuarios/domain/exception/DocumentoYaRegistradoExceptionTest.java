package com.plazoleta.usuarios.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DocumentoYaRegistradoExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensajePorDefecto() {
        // Act
        DocumentoYaRegistradoException exception = new DocumentoYaRegistradoException();

        // Assert
        assertNotNull(exception);
        assertEquals("Ya existe un usuario con este número de documento", exception.getMessage());
    }

    @Test
    void deberiaCrearExcepcionConMensajePersonalizado() {
        // Arrange
        String mensajePersonalizado = "El documento ya está registrado";

        // Act
        DocumentoYaRegistradoException exception = new DocumentoYaRegistradoException(mensajePersonalizado);

        // Assert
        assertNotNull(exception);
        assertEquals(mensajePersonalizado, exception.getMessage());
    }
}
