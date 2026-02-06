package com.plazoleta.usuarios.infraestructure.input.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.plazoleta.usuarios.application.dto.CrearEmpleadoDto;
import com.plazoleta.usuarios.application.dto.CrearPropietarioDto;
import com.plazoleta.usuarios.application.dto.response.UsuarioResponseDto;
import com.plazoleta.usuarios.application.handler.IUsuarioHandler;
import com.plazoleta.usuarios.domain.exception.CampoInvalidoException;
import com.plazoleta.usuarios.domain.model.TokenClaims;
import com.plazoleta.usuarios.domain.exception.EmailInvalidoException;
import com.plazoleta.usuarios.domain.exception.UsuarioMayorDeEdadException;
import com.plazoleta.usuarios.infraestructure.exceptionhandler.GlobalExceptionHandler;
import com.plazoleta.usuarios.infraestructure.input.rest.dto.CrearEmpleadoRequestDto;
import com.plazoleta.usuarios.infraestructure.input.rest.dto.CrearPropietarioRequestDto;
import com.plazoleta.usuarios.infraestructure.input.rest.mapper.CrearEmpleadoRestMapper;
import com.plazoleta.usuarios.infraestructure.input.rest.mapper.CrearPropietarioRestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private IUsuarioHandler usuarioHandler;

    @Mock
    private CrearPropietarioRestMapper crearPropietarioRestMapper;

    @Mock
    private CrearEmpleadoRestMapper crearEmpleadoRestMapper;

    @InjectMocks
    private UsuarioController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void deberiaCrearPropietarioYRetornar201() throws Exception {
        // Arrange
        CrearPropietarioRequestDto requestDto = crearRequestDtoValido();
        CrearPropietarioDto applicationDto = new CrearPropietarioDto();

        when(crearPropietarioRestMapper.toApplicationDto(any(CrearPropietarioRequestDto.class))).thenReturn(applicationDto);
        doNothing().when(usuarioHandler).crearPropietario(any(CrearPropietarioDto.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(crearPropietarioRestMapper, times(1)).toApplicationDto(any(CrearPropietarioRequestDto.class));
        verify(usuarioHandler, times(1)).crearPropietario(any(CrearPropietarioDto.class));
    }

    @Test
    void deberiaRetornar400CuandoNombreEsNulo() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setNombre(null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
                .andExpect(jsonPath("$.mensaje").value("Los datos enviados no cumplen con las validaciones requeridas"))
                .andExpect(jsonPath("$.errores.nombre").exists());

        verify(usuarioHandler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoNombreEsVacio() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setNombre("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").exists());

        verify(usuarioHandler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoCorreoEsInvalido() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setCorreo("correo-invalido");

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.correo").exists());

        verify(usuarioHandler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoDocumentoContieneLetras() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setDocumento("ABC123");

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.documento").exists());

        verify(usuarioHandler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoCelularEsInvalido() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setCelular("celular-invalido");

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.celular").exists());

        verify(usuarioHandler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoFechaNacimientoEsFutura() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setFechaNacimiento(LocalDate.now().plusDays(1));

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.fechaNacimiento").exists());

        verify(usuarioHandler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoClaveEsMuyCorta() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setClave("12345"); // Menos de 6 caracteres

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.clave").exists());

        verify(usuarioHandler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoHandlerLanzaCampoInvalidoException() throws Exception {
        // Arrange
        CrearPropietarioRequestDto requestDto = crearRequestDtoValido();
        CrearPropietarioDto applicationDto = new CrearPropietarioDto();

        when(crearPropietarioRestMapper.toApplicationDto(any(CrearPropietarioRequestDto.class))).thenReturn(applicationDto);
        doThrow(new CampoInvalidoException("Documento inválido"))
                .when(usuarioHandler).crearPropietario(any(CrearPropietarioDto.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("CAMPO_INVALIDO"))
                .andExpect(jsonPath("$.mensaje").value("Documento inválido"));
    }

    @Test
    void deberiaRetornar400CuandoHandlerLanzaEmailInvalidoException() throws Exception {
        // Arrange
        CrearPropietarioRequestDto requestDto = crearRequestDtoValido();
        CrearPropietarioDto applicationDto = new CrearPropietarioDto();

        when(crearPropietarioRestMapper.toApplicationDto(any(CrearPropietarioRequestDto.class))).thenReturn(applicationDto);
        doThrow(new EmailInvalidoException())
                .when(usuarioHandler).crearPropietario(any(CrearPropietarioDto.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("EMAIL_INVALIDO"));
    }

    @Test
    void deberiaRetornar400CuandoHandlerLanzaUsuarioMayorDeEdadException() throws Exception {
        // Arrange
        CrearPropietarioRequestDto requestDto = crearRequestDtoValido();
        CrearPropietarioDto applicationDto = new CrearPropietarioDto();

        when(crearPropietarioRestMapper.toApplicationDto(any(CrearPropietarioRequestDto.class))).thenReturn(applicationDto);
        doThrow(new UsuarioMayorDeEdadException())
                .when(usuarioHandler).crearPropietario(any(CrearPropietarioDto.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("EDAD_INSUFICIENTE"));
    }

    @Test
    void deberiaValidarTodosLosCamposObligatorios() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = new CrearPropietarioRequestDto();
        // Todos los campos nulos

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.apellido").exists())
                .andExpect(jsonPath("$.errores.documento").exists())
                .andExpect(jsonPath("$.errores.celular").exists())
                .andExpect(jsonPath("$.errores.fechaNacimiento").exists())
                .andExpect(jsonPath("$.errores.correo").exists())
                .andExpect(jsonPath("$.errores.clave").exists());

        verify(usuarioHandler, never()).crearPropietario(any());
    }

    @Test
    void deberiaObtenerUsuarioPorIdYRetornar200() throws Exception {
        // Arrange
        Integer id = 1;
        UsuarioResponseDto responseDto = crearUsuarioResponseDto();

        when(usuarioHandler.obtenerUsuarioPorId(id)).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/usuarios/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.nombre").value(responseDto.getNombre()))
                .andExpect(jsonPath("$.apellido").value(responseDto.getApellido()))
                .andExpect(jsonPath("$.documento").value(responseDto.getDocumento()))
                .andExpect(jsonPath("$.celular").value(responseDto.getCelular()))
                .andExpect(jsonPath("$.correo").value(responseDto.getCorreo()))
                .andExpect(jsonPath("$.rol").value(responseDto.getRol()));

        verify(usuarioHandler, times(1)).obtenerUsuarioPorId(id);
    }

    @Test
    void deberiaRetornar404CuandoUsuarioNoExiste() throws Exception {
        // Arrange
        Integer id = 999;

        when(usuarioHandler.obtenerUsuarioPorId(id)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/v1/usuarios/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(usuarioHandler, times(1)).obtenerUsuarioPorId(id);
    }

    // Metodos auxiliares
    private UsuarioResponseDto crearUsuarioResponseDto() {
        return UsuarioResponseDto.builder()
                .id(1)
                .nombre("Juan")
                .apellido("Pérez")
                .documento("12345678")
                .celular("+573001234567")
                .fechaNacimiento(LocalDate.now().minusYears(25))
                .correo("juan@example.com")
                .rol("PROPIETARIO")
                .build();
    }

    @Test
    void deberiaCrearEmpleadoYRetornar201() throws Exception {
        // Arrange: tokenClaims con restauranteId para que el controller asocie el empleado al restaurante
        TokenClaims tokenClaims = new TokenClaims(1, "propietario@mail.com", "PROPIETARIO", 1);
        CrearEmpleadoRequestDto requestDto = crearEmpleadoRequestDtoValido();
        CrearEmpleadoDto applicationDto = new CrearEmpleadoDto();

        when(crearEmpleadoRestMapper.toApplicationDto(any(CrearEmpleadoRequestDto.class))).thenReturn(applicationDto);
        doNothing().when(usuarioHandler).crearEmpleado(any(CrearEmpleadoDto.class));

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/empleado")
                        .with(request -> {
                            request.setAttribute("tokenClaims", tokenClaims);
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(crearEmpleadoRestMapper, times(1)).toApplicationDto(any(CrearEmpleadoRequestDto.class));
        verify(usuarioHandler, times(1)).crearEmpleado(any(CrearEmpleadoDto.class));
    }

    @Test
    void deberiaRetornar400CuandoCrearEmpleadoConNombreVacio() throws Exception {
        // Arrange
        CrearEmpleadoRequestDto dto = crearEmpleadoRequestDtoValido();
        dto.setNombre("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/usuarios/empleado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").exists());

        verify(usuarioHandler, never()).crearEmpleado(any());
    }

    private CrearPropietarioRequestDto crearRequestDtoValido() {
        CrearPropietarioRequestDto dto = new CrearPropietarioRequestDto();
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setDocumento("12345678");
        dto.setCelular("+573001234567");
        dto.setFechaNacimiento(LocalDate.now().minusYears(25));
        dto.setCorreo("juan@example.com");
        dto.setClave("password123");
        return dto;
    }

    private CrearEmpleadoRequestDto crearEmpleadoRequestDtoValido() {
        CrearEmpleadoRequestDto dto = new CrearEmpleadoRequestDto();
        dto.setNombre("Pedro");
        dto.setApellido("García");
        dto.setDocumento("87654321");
        dto.setCelular("+573009876543");
        dto.setFechaNacimiento(LocalDate.now().minusYears(22));
        dto.setCorreo("pedro@restaurante.com");
        dto.setClave("empleado123");
        return dto;
    }
}