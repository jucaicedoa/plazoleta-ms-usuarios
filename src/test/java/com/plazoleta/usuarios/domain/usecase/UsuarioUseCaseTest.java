package com.plazoleta.usuarios.domain.usecase;

import com.plazoleta.usuarios.domain.exception.CampoInvalidoException;
import com.plazoleta.usuarios.domain.exception.EmailInvalidoException;
import com.plazoleta.usuarios.domain.exception.UsuarioMayorDeEdadException;
import com.plazoleta.usuarios.domain.model.DatosCreacionUsuario;
import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioPersistencePort persistencePort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private UsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UsuarioUseCase(persistencePort, passwordEncoderPort);
    }

    @Test
    void deberiaCrearPropietarioConDatosValidos() {
        // Arrange
        DatosCreacionUsuario datos = crearDatosValidos();

        when(persistencePort.existeCorreo(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("passwordEncriptada");
        when(persistencePort.guardarUsuario(any(Usuario.class))).thenReturn(new Usuario());

        // Act
        useCase.crearPropietario(datos);

        // Assert
        verify(persistencePort, times(1)).existeCorreo(datos.getCorreo());
        verify(passwordEncoderPort, times(1)).encode("password123");
        verify(persistencePort, times(1)).guardarUsuario(any(Usuario.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123ABC456", ""})
    void deberiaLanzarExcepcionCuandoDocumentoEsInvalido(String documentoInvalido) {
        // Arrange
        DatosCreacionUsuario datos = DatosCreacionUsuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documento(documentoInvalido)
                .celular("+573001234567")
                .fechaNacimiento(LocalDate.now().minusYears(25))
                .correo("juan@example.com")
                .clave("password123")
                .build();

        // Act & Assert
        CampoInvalidoException exception = assertThrows(
                CampoInvalidoException.class,
                () -> useCase.crearPropietario(datos)
        );

        assertEquals("Documento inválido", exception.getMessage());
        verify(persistencePort, never()).guardarUsuario(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"correo-invalido", "correo@"})
    void deberiaLanzarExcepcionCuandoCorreoEsInvalido(String correoInvalido) {
        // Arrange
        DatosCreacionUsuario datos = DatosCreacionUsuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular("+573001234567")
                .fechaNacimiento(LocalDate.now().minusYears(25))
                .correo(correoInvalido)
                .clave("password123")
                .build();

        // Act & Assert
        EmailInvalidoException exception = assertThrows(
                EmailInvalidoException.class,
                () -> useCase.crearPropietario(datos)
        );

        assertEquals("El correo electrónico no tiene un formato válido", exception.getMessage());
        verify(persistencePort, never()).guardarUsuario(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"celular-invalido", "+12345678901234"})
    void deberiaLanzarExcepcionCuandoCelularEsInvalido(String celularInvalido) {
        // Arrange
        DatosCreacionUsuario datos = DatosCreacionUsuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular(celularInvalido)
                .fechaNacimiento(LocalDate.now().minusYears(25))
                .correo("juan@example.com")
                .clave("password123")
                .build();

        // Act & Assert
        CampoInvalidoException exception = assertThrows(
                CampoInvalidoException.class,
                () -> useCase.crearPropietario(datos)
        );

        assertEquals("Celular inválido", exception.getMessage());
        verify(persistencePort, never()).guardarUsuario(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"+573001234567", "3001234567"})
    void deberiaAceptarCelularValido(String celularValido) {
        // Arrange
        DatosCreacionUsuario datos = DatosCreacionUsuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular(celularValido)
                .fechaNacimiento(LocalDate.now().minusYears(25))
                .correo("juan@example.com")
                .clave("password123")
                .build();

        when(persistencePort.existeCorreo(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("passwordEncriptada");
        when(persistencePort.guardarUsuario(any(Usuario.class))).thenReturn(new Usuario());

        // Act
        useCase.crearPropietario(datos);

        // Assert
        verify(persistencePort, times(1)).guardarUsuario(any(Usuario.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoUsuarioEsMenorDe18Anios() {
        // Arrange
        DatosCreacionUsuario datos = DatosCreacionUsuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular("+573001234567")
                .fechaNacimiento(LocalDate.now().minusYears(17)) // Usuario con 17 años completos (menor de 18)
                .correo("juan@example.com")
                .clave("password123")
                .build();

        // Act & Assert
        UsuarioMayorDeEdadException exception = assertThrows(
                UsuarioMayorDeEdadException.class,
                () -> useCase.crearPropietario(datos)
        );

        assertTrue(exception.getMessage().contains("mayor"));
        verify(persistencePort, never()).guardarUsuario(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoCorreoYaExiste() {
        // Arrange
        DatosCreacionUsuario datos = crearDatosValidos();

        when(persistencePort.existeCorreo(anyString())).thenReturn(true);

        // Act & Assert
        CampoInvalidoException exception = assertThrows(
                CampoInvalidoException.class,
                () -> useCase.crearPropietario(datos)
        );

        assertEquals("Correo ya registrado", exception.getMessage());
        verify(persistencePort, times(1)).existeCorreo(datos.getCorreo());
        verify(persistencePort, never()).guardarUsuario(any());
    }

    @Test
    void deberiaEncriptarClaveAntesDeGuardar() {
        // Arrange
        DatosCreacionUsuario datos = crearDatosValidos();
        String claveOriginal = datos.getClave();
        String claveEncriptada = "passwordEncriptada";
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

        when(persistencePort.existeCorreo(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(claveOriginal)).thenReturn(claveEncriptada);
        when(persistencePort.guardarUsuario(any(Usuario.class))).thenReturn(new Usuario());

        // Act
        useCase.crearPropietario(datos);

        // Assert
        verify(passwordEncoderPort, times(1)).encode(claveOriginal);
        verify(persistencePort).guardarUsuario(usuarioCaptor.capture());
        assertEquals(claveEncriptada, usuarioCaptor.getValue().getClave());
    }

    @Test
    void deberiaAsignarRolPropietarioAlCrear() {
        // Arrange
        DatosCreacionUsuario datos = crearDatosValidos();
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

        when(persistencePort.existeCorreo(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("passwordEncriptada");
        when(persistencePort.guardarUsuario(any(Usuario.class))).thenReturn(new Usuario());

        // Act
        useCase.crearPropietario(datos);

        // Assert
        verify(persistencePort).guardarUsuario(usuarioCaptor.capture());
        Usuario usuario = usuarioCaptor.getValue();
        assertNotNull(usuario.getRol());
        assertEquals("PROPIETARIO", usuario.getRol().getNombre());
        // El id no se fija en el dominio; el adapter resuelve el rol por nombre en BD
    }

    @Test
    void deberiaAceptarCorreoConGuionYGuionBajo() {
        // Arrange
        DatosCreacionUsuario datos = DatosCreacionUsuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular("+573001234567")
                .fechaNacimiento(LocalDate.now().minusYears(25))
                .correo("usuario_test-123@example.com")
                .clave("password123")
                .build();

        when(persistencePort.existeCorreo(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("passwordEncriptada");
        when(persistencePort.guardarUsuario(any(Usuario.class))).thenReturn(new Usuario());

        // Act
        useCase.crearPropietario(datos);

        // Assert
        verify(persistencePort, times(1)).guardarUsuario(any(Usuario.class));
    }

    @Test
    void deberiaAceptarUsuarioDe18AniosExactos() {
        // Arrange
        DatosCreacionUsuario datos = DatosCreacionUsuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular("+573001234567")
                .fechaNacimiento(LocalDate.now().minusYears(18))
                .correo("juan@example.com")
                .clave("password123")
                .build();

        when(persistencePort.existeCorreo(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("passwordEncriptada");
        when(persistencePort.guardarUsuario(any(Usuario.class))).thenReturn(new Usuario());

        // Act
        useCase.crearPropietario(datos);

        // Assert
        verify(persistencePort, times(1)).guardarUsuario(any(Usuario.class));
    }

    @Test
    void deberiaCrearEmpleadoConDatosValidos() {
        // Arrange
        DatosCreacionUsuario datos = crearDatosValidos();

        when(persistencePort.existeCorreo(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("passwordEncriptada");
        when(persistencePort.guardarUsuario(any(Usuario.class))).thenReturn(new Usuario());

        // Act
        useCase.crearEmpleado(datos);

        // Assert
        verify(persistencePort, times(1)).existeCorreo(datos.getCorreo());
        verify(passwordEncoderPort, times(1)).encode("password123");
        verify(persistencePort, times(1)).guardarUsuario(any(Usuario.class));
    }

    @Test
    void deberiaAsignarRolEmpleadoAlCrear() {
        // Arrange
        DatosCreacionUsuario datos = crearDatosValidos();
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

        when(persistencePort.existeCorreo(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("passwordEncriptada");
        when(persistencePort.guardarUsuario(any(Usuario.class))).thenReturn(new Usuario());

        // Act
        useCase.crearEmpleado(datos);

        // Assert
        verify(persistencePort).guardarUsuario(usuarioCaptor.capture());
        Usuario usuario = usuarioCaptor.getValue();
        assertNotNull(usuario.getRol());
        assertEquals("EMPLEADO", usuario.getRol().getNombre());
    }

    @Test
    void deberiaLanzarExcepcionCuandoCorreoYaExisteEnCrearEmpleado() {
        // Arrange
        DatosCreacionUsuario datos = crearDatosValidos();

        when(persistencePort.existeCorreo(anyString())).thenReturn(true);

        // Act & Assert
        CampoInvalidoException exception = assertThrows(
                CampoInvalidoException.class,
                () -> useCase.crearEmpleado(datos)
        );

        assertEquals("Correo ya registrado", exception.getMessage());
        verify(persistencePort, times(1)).existeCorreo(datos.getCorreo());
        verify(persistencePort, never()).guardarUsuario(any());
    }

    // Metodo auxiliar para crear datos válidos
    private DatosCreacionUsuario crearDatosValidos() {
        return DatosCreacionUsuario.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular("+573001234567")
                .fechaNacimiento(LocalDate.now().minusYears(25))
                .correo("juan@example.com")
                .clave("password123")
                .build();
    }
}