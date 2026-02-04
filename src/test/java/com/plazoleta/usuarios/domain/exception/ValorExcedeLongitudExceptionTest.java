package com.plazoleta.usuarios.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ValorExcedeLongitudExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensajeYCampo() {
        // Arrange
        String mensaje = "El valor excede la longitud máxima";
        String campo = "nombre";

        // Act
        ValorExcedeLongitudException exception = new ValorExcedeLongitudException(mensaje, campo);

        // Assert
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
        assertEquals(campo, exception.getCampo());
    }

    @Test
    void deberiaUsarDesconocidoCuandoCampoEsNull() {
        // Arrange
        String mensaje = "El valor excede la longitud máxima";

        // Act
        ValorExcedeLongitudException exception = new ValorExcedeLongitudException(mensaje, null);

        // Assert
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
        assertEquals("desconocido", exception.getCampo());
    }

    @Test
    void deberiaAceptarCampoVacio() {
        // Arrange
        String mensaje = "El valor excede la longitud máxima";
        String campoVacio = "";

        // Act
        ValorExcedeLongitudException exception = new ValorExcedeLongitudException(mensaje, campoVacio);

        // Assert
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
        assertEquals("", exception.getCampo()); // Se acepta vacío, solo null se convierte a "desconocido"
    }
}
