package com.plazoleta.usuarios.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsuarioTest {

    @Test
    void deberiaCrearUsuarioConDatosValidos() {
        // Arrange
        LocalDate fechaNacimiento = LocalDate.now().minusYears(20);
        Rol rol = new Rol(2, "PROPIETARIO");

        DatosCreacionUsuario datos = DatosCreacionUsuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular("+573001234567")
                .fechaNacimiento(fechaNacimiento)
                .correo("juan@example.com")
                .clave("password123")
                .build();

        // Act
        Usuario usuario = Usuario.crear(datos, rol);

        // Assert
        assertNotNull(usuario);
        assertEquals("Juan", usuario.getNombre());
        assertEquals("Pérez", usuario.getApellido());
        assertEquals("12345678", usuario.getDocumento());
        assertEquals("+573001234567", usuario.getCelular());
        assertEquals(fechaNacimiento, usuario.getFechaNacimiento());
        assertEquals("juan@example.com", usuario.getCorreo());
        assertEquals("password123", usuario.getClave());
        assertEquals(rol, usuario.getRol());
    }

    @Test
    void deberiaLanzarExcepcionCuandoUsuarioEsMenorDeEdad() {
        // Arrange
        LocalDate fechaNacimiento = LocalDate.now().minusYears(17);
        Rol rol = new Rol(2, "PROPIETARIO");

        DatosCreacionUsuario datos = DatosCreacionUsuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular("+573001234567")
                .fechaNacimiento(fechaNacimiento)
                .correo("juan@example.com")
                .clave("password123")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Usuario.crear(datos, rol)
        );

        assertEquals("El usuario debe ser mayor de edad", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionCuandoFechaNacimientoEsNull() {
        // Arrange
        Rol rol = new Rol(2, "PROPIETARIO");

        DatosCreacionUsuario datos = DatosCreacionUsuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular("+573001234567")
                .fechaNacimiento(null)
                .correo("juan@example.com")
                .clave("password123")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Usuario.crear(datos, rol)
        );

        assertEquals("El usuario debe ser mayor de edad", exception.getMessage());
    }

    @Test
    void deberiaCrearUsuarioConExactamente18Anios() {
        // Arrange
        LocalDate fechaNacimiento = LocalDate.now().minusYears(18);
        Rol rol = new Rol(2, "PROPIETARIO");

        DatosCreacionUsuario datos = DatosCreacionUsuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular("+573001234567")
                .fechaNacimiento(fechaNacimiento)
                .correo("juan@example.com")
                .clave("password123")
                .build();

        // Act
        Usuario usuario = Usuario.crear(datos, rol);

        // Assert
        assertNotNull(usuario);
        assertEquals(18, java.time.Period.between(usuario.getFechaNacimiento(), LocalDate.now()).getYears());
    }

    @Test
    void deberiaPermitirCrearUsuarioConConstructorVacio() {
        // Act
        Usuario usuario = new Usuario();

        // Assert
        assertNotNull(usuario);
        assertNull(usuario.getNombre());
        assertNull(usuario.getApellido());
    }

    @Test
    void deberiaPermitirModificarAtributosConSetters() {
        // Arrange
        Usuario usuario = new Usuario();
        Rol rol = new Rol(1, "ADMINISTRADOR");

        // Act
        usuario.setId(1);
        usuario.setNombre("Carlos");
        usuario.setApellido("García");
        usuario.setDocumento("87654321");
        usuario.setCelular("+573009876543");
        usuario.setFechaNacimiento(LocalDate.now().minusYears(25));
        usuario.setCorreo("carlos@example.com");
        usuario.setClave("newpassword");
        usuario.setRol(rol);

        // Assert
        assertEquals(1, usuario.getId());
        assertEquals("Carlos", usuario.getNombre());
        assertEquals("García", usuario.getApellido());
        assertEquals("87654321", usuario.getDocumento());
        assertEquals("+573009876543", usuario.getCelular());
        assertEquals("carlos@example.com", usuario.getCorreo());
        assertEquals("newpassword", usuario.getClave());
        assertEquals(rol, usuario.getRol());
    }
}