package com.plazoleta.usuarios.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ValueExceedsLengthExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensajeYCampo() {
        // Arrange
        String mensaje = "El valor excede la longitud máxima";
        String campo = "nombre";

        // Act
        ValueExceedsLengthException exception = new ValueExceedsLengthException(mensaje, campo);

        // Assert
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
        assertEquals(campo, exception.getField());
    }

    @Test
    void deberiaUsarDesconocidoCuandoCampoEsNull() {
        // Arrange
        String mensaje = "El valor excede la longitud máxima";

        // Act
        ValueExceedsLengthException exception = new ValueExceedsLengthException(mensaje, null);

        // Assert
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
        assertEquals("unknown", exception.getField());
    }

    @Test
    void deberiaAceptarCampoVacio() {
        // Arrange
        String mensaje = "El valor excede la longitud máxima";
        String campoVacio = "";

        // Act
        ValueExceedsLengthException exception = new ValueExceedsLengthException(mensaje, campoVacio);

        // Assert
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
        assertEquals("", exception.getField()); // Se acepta vacío, solo null se convierte a "desconocido"
    }
}
