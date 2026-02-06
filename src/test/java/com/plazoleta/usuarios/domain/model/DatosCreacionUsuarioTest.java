package com.plazoleta.usuarios.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatosCreacionUsuarioTest {

    @Test
    void deberiaCrearDatosCreacionUsuarioConBuilder() {
        // Arrange
        LocalDate fechaNacimiento = LocalDate.now().minusYears(25);

        // Act
        DatosCreacionUsuario datos = DatosCreacionUsuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular("+573001234567")
                .fechaNacimiento(fechaNacimiento)
                .correo("juan@example.com")
                .clave("password123")
                .restauranteId(null)
                .build();

        // Assert
        assertNotNull(datos);
        assertEquals("Juan", datos.getNombre());
        assertEquals("Pérez", datos.getApellido());
        assertEquals("12345678", datos.getDocumento());
        assertEquals("+573001234567", datos.getCelular());
        assertEquals(fechaNacimiento, datos.getFechaNacimiento());
        assertEquals("juan@example.com", datos.getCorreo());
        assertEquals("password123", datos.getClave());
    }

    @Test
    void deberiaCrearDatosCreacionUsuarioConConstructor() {
        // Arrange
        LocalDate fechaNacimiento = LocalDate.now().minusYears(30);

        // Act
        DatosCreacionUsuario datos = new DatosCreacionUsuario(
                "María",
                "García",
                "87654321",
                "+573009876543",
                fechaNacimiento,
                "maria@example.com",
                "securepass",
                null
        );

        // Assert
        assertNotNull(datos);
        assertEquals("María", datos.getNombre());
        assertEquals("García", datos.getApellido());
        assertEquals("87654321", datos.getDocumento());
        assertEquals("+573009876543", datos.getCelular());
        assertEquals(fechaNacimiento, datos.getFechaNacimiento());
        assertEquals("maria@example.com", datos.getCorreo());
        assertEquals("securepass", datos.getClave());
    }
}