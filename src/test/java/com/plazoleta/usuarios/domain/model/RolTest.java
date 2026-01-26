package com.plazoleta.usuarios.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RolTest {

    @Test
    void deberiaCrearRolConConstructorCompleto() {
        // Act
        Rol rol = new Rol(1, "ADMINISTRADOR");

        // Assert
        assertNotNull(rol);
        assertEquals(1, rol.getId());
        assertEquals("ADMINISTRADOR", rol.getNombre());
    }

    @Test
    void deberiaCrearRolConConstructorVacio() {
        // Act
        Rol rol = new Rol();

        // Assert
        assertNotNull(rol);
        // No verificamos el id ya que el valor por defecto de int es 0
        assertNull(rol.getNombre());
    }

    @Test
    void deberiaPermitirModificarAtributosConSetters() {
        // Arrange
        Rol rol = new Rol();

        // Act
        rol.setId(2);
        rol.setNombre("PROPIETARIO");

        // Assert
        assertEquals(2, rol.getId());
        assertEquals("PROPIETARIO", rol.getNombre());
    }

    @Test
    void deberiaImplementarEqualsYHashCodeCorrectamente() {
        // Arrange
        Rol rol1 = new Rol(1, "ADMINISTRADOR");
        Rol rol2 = new Rol(1, "ADMINISTRADOR");
        Rol rol3 = new Rol(2, "PROPIETARIO");

        // Assert
        assertEquals(rol1, rol2);
        assertNotEquals(rol1, rol3);
        assertEquals(rol1.hashCode(), rol2.hashCode());
    }

    @Test
    void deberiaImplementarToStringCorrectamente() {
        // Arrange
        Rol rol = new Rol(1, "ADMINISTRADOR");

        // Act
        String resultado = rol.toString();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.contains("1"));
        assertTrue(resultado.contains("ADMINISTRADOR"));
    }
}