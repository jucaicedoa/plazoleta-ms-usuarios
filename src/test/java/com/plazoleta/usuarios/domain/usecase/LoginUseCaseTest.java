package com.plazoleta.usuarios.domain.usecase;

import com.plazoleta.usuarios.domain.exception.CredencialesInvalidasException;
import com.plazoleta.usuarios.domain.model.Rol;
import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.domain.spi.JwtProviderPort;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UsuarioPersistencePort usuarioPersistencePort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private JwtProviderPort jwtProviderPort;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(usuarioPersistencePort, passwordEncoderPort, jwtProviderPort);
    }

    @Test
    void deberiaRetornarTokenCuandoCredencialesSonCorrectas() {
        // Arrange
        String correo = "usuario@mail.com";
        String clave = "clave123";
        Usuario usuario = crearUsuarioConRol(correo, "claveEncriptada", "PROPIETARIO");
        String tokenEsperado = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

        when(usuarioPersistencePort.buscarPorCorreo(correo)).thenReturn(Optional.of(usuario));
        when(passwordEncoderPort.matches(clave, usuario.getClave())).thenReturn(true);
        when(jwtProviderPort.generarToken(usuario.getId(), usuario.getCorreo(), "PROPIETARIO")).thenReturn(tokenEsperado);

        // Act
        String token = loginUseCase.login(correo, clave);

        // Assert
        assertEquals(tokenEsperado, token);
        verify(usuarioPersistencePort).buscarPorCorreo(correo);
        verify(passwordEncoderPort).matches(clave, usuario.getClave());
        verify(jwtProviderPort).generarToken(usuario.getId(), usuario.getCorreo(), "PROPIETARIO");
    }

    @Test
    void deberiaLanzarCredencialesInvalidasCuandoUsuarioNoExiste() {
        // Arrange
        String correo = "noexiste@mail.com";
        String clave = "clave123";

        when(usuarioPersistencePort.buscarPorCorreo(correo)).thenReturn(Optional.empty());

        // Act & Assert
        CredencialesInvalidasException exception = assertThrows(
                CredencialesInvalidasException.class,
                () -> loginUseCase.login(correo, clave)
        );
        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(usuarioPersistencePort).buscarPorCorreo(correo);
        verify(passwordEncoderPort, org.mockito.Mockito.never()).matches(anyString(), anyString());
        verify(jwtProviderPort, org.mockito.Mockito.never()).generarToken(anyInt(), anyString(), anyString());
    }

    @Test
    void deberiaLanzarCredencialesInvalidasCuandoClaveEsIncorrecta() {
        // Arrange
        String correo = "usuario@mail.com";
        String clave = "claveIncorrecta";
        Usuario usuario = crearUsuarioConRol(correo, "claveEncriptada", "PROPIETARIO");

        when(usuarioPersistencePort.buscarPorCorreo(correo)).thenReturn(Optional.of(usuario));
        when(passwordEncoderPort.matches(clave, usuario.getClave())).thenReturn(false);

        // Act & Assert
        CredencialesInvalidasException exception = assertThrows(
                CredencialesInvalidasException.class,
                () -> loginUseCase.login(correo, clave)
        );
        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(usuarioPersistencePort).buscarPorCorreo(correo);
        verify(passwordEncoderPort).matches(clave, usuario.getClave());
        verify(jwtProviderPort, org.mockito.Mockito.never()).generarToken(anyInt(), anyString(), anyString());
    }

    private Usuario crearUsuarioConRol(String correo, String claveEncriptada, String nombreRol) {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setCorreo(correo);
        usuario.setClave(claveEncriptada);
        usuario.setRol(new Rol(null, nombreRol));
        return usuario;
    }
}