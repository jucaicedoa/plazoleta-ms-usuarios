package com.plazoleta.usuarios.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CampoObligatorioExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensajePorDefecto() {
        // Act
        CampoObligatorioException exception = new CampoObligatorioException();

        // Assert
        assertNotNull(exception);
        assertEquals("Falta un campo obligatorio", exception.getMessage());
    }

    @Test
    void deberiaCrearExcepcionConMensajePersonalizado() {
        // Arrange
        String mensajePersonalizado = "El campo nombre es obligatorio";

        // Act
        CampoObligatorioException exception = new CampoObligatorioException(mensajePersonalizado);

        // Assert
        assertNotNull(exception);
        assertEquals(mensajePersonalizado, exception.getMessage());
    }
}
