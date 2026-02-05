package com.plazoleta.usuarios.domain.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CredencialesInvalidasExceptionTest {

    @Test
    void deberiaCrearExcepcionConMensaje() {
        String mensaje = "Credenciales inválidas";

        CredencialesInvalidasException exception = new CredencialesInvalidasException(mensaje);

        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
    }
}