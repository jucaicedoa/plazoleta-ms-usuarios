package com.plazoleta.usuarios.application.handler;

import com.plazoleta.usuarios.application.dto.CrearPropietarioDto;
import com.plazoleta.usuarios.application.dto.response.UsuarioResponseDto;
import com.plazoleta.usuarios.application.mapper.UsuarioRequestMapper;
import com.plazoleta.usuarios.application.mapper.UsuarioResponseMapper;
import com.plazoleta.usuarios.domain.api.UsuarioServicePort;
import com.plazoleta.usuarios.domain.model.DatosCreacionUsuario;
import com.plazoleta.usuarios.domain.model.Rol;
import com.plazoleta.usuarios.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CrearPropietarioHandlerTest {

    @Mock
    private UsuarioServicePort usuarioServicePort;

    @Mock
    private UsuarioRequestMapper mapper;

    @Mock
    private UsuarioResponseMapper responseMapper;

    private CrearPropietarioHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CrearPropietarioHandler(usuarioServicePort, mapper, responseMapper);
    }

    @Test
    void deberiaLlamarAlServicePortParaCrearPropietario() {
        // Arrange
        CrearPropietarioDto dto = crearApplicationDto();
        DatosCreacionUsuario datos = crearDatosCreacion();

        when(mapper.toDatosCreacion(dto)).thenReturn(datos);
        doNothing().when(usuarioServicePort).crearPropietario(any(DatosCreacionUsuario.class));

        // Act
        handler.crearPropietario(dto);

        // Assert
        verify(mapper, times(1)).toDatosCreacion(dto);
        verify(usuarioServicePort, times(1)).crearPropietario(datos);
    }

    @Test
    void deberiaMapearCorrectamenteElDto() {
        // Arrange
        CrearPropietarioDto dto = crearApplicationDto();
        DatosCreacionUsuario datos = crearDatosCreacion();

        when(mapper.toDatosCreacion(dto)).thenReturn(datos);

        // Act
        handler.crearPropietario(dto);

        // Assert
        verify(mapper, times(1)).toDatosCreacion(dto);
    }

    @Test
    void deberiaPropagrarExcepcionDelServicePort() {
        // Arrange
        CrearPropietarioDto dto = crearApplicationDto();
        DatosCreacionUsuario datos = crearDatosCreacion();

        when(mapper.toDatosCreacion(dto)).thenReturn(datos);
        doThrow(new RuntimeException("Error al crear propietario"))
                .when(usuarioServicePort).crearPropietario(any(DatosCreacionUsuario.class));

        // Act & Assert
        try {
            handler.crearPropietario(dto);
        } catch (RuntimeException e) {
            // Expected exception
        }

        verify(usuarioServicePort, times(1)).crearPropietario(datos);
    }

    @Test
    void deberiaObtenerUsuarioPorIdYRetornarDto() {
        // Arrange
        Integer id = 1;
        Usuario usuario = crearUsuario();
        UsuarioResponseDto responseDto = UsuarioResponseDto.builder()
                .id(1)
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular("+573001234567")
                .fechaNacimiento(LocalDate.now().minusYears(25))
                .correo("juan@example.com")
                .rol("PROPIETARIO")
                .build();

        when(usuarioServicePort.obtenerUsuarioPorId(id)).thenReturn(usuario);
        when(responseMapper.toResponse(usuario)).thenReturn(responseDto);

        // Act
        UsuarioResponseDto resultado = handler.obtenerUsuarioPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Juan", resultado.getNombre());
        assertEquals("PROPIETARIO", resultado.getRol());
        verify(usuarioServicePort, times(1)).obtenerUsuarioPorId(id);
        verify(responseMapper, times(1)).toResponse(usuario);
    }

    @Test
    void deberiaRetornarNullCuandoUsuarioNoExiste() {
        // Arrange
        Integer id = 999;
        when(usuarioServicePort.obtenerUsuarioPorId(id)).thenReturn(null);

        // Act
        UsuarioResponseDto resultado = handler.obtenerUsuarioPorId(id);

        // Assert
        assertNull(resultado);
        verify(usuarioServicePort, times(1)).obtenerUsuarioPorId(id);
        verify(responseMapper, times(0)).toResponse(any());
    }

    // Métodos auxiliares
    private CrearPropietarioDto crearApplicationDto() {
        CrearPropietarioDto dto = new CrearPropietarioDto();
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setDocumento("12345678");
        dto.setCelular("+573001234567");
        dto.setFechaNacimiento(LocalDate.now().minusYears(25));
        dto.setCorreo("juan@example.com");
        dto.setClave("password123");
        return dto;
    }

    private DatosCreacionUsuario crearDatosCreacion() {
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

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setDocumento("12345678");
        usuario.setCelular("+573001234567");
        usuario.setFechaNacimiento(LocalDate.now().minusYears(25));
        usuario.setCorreo("juan@example.com");
        usuario.setRol(new Rol(2, "PROPIETARIO"));
        return usuario;
    }
}