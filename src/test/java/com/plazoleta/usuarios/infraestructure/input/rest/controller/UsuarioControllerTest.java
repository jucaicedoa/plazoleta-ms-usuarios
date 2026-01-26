package com.plazoleta.usuarios.infraestructure.input.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.plazoleta.usuarios.application.dto.CrearPropietarioDto;
import com.plazoleta.usuarios.application.handler.CrearPropietarioHandler;
import com.plazoleta.usuarios.domain.exception.CampoInvalidoException;
import com.plazoleta.usuarios.domain.exception.EmailInvalidoException;
import com.plazoleta.usuarios.domain.exception.UsuarioMayorDeEdadException;
import com.plazoleta.usuarios.infraestructure.exceptionhandler.GlobalExceptionHandler;
import com.plazoleta.usuarios.infraestructure.input.rest.dto.CrearPropietarioRequestDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private CrearPropietarioHandler handler;

    @Mock
    private CrearPropietarioRestMapper restMapper;

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

        when(restMapper.toApplicationDto(any(CrearPropietarioRequestDto.class))).thenReturn(applicationDto);
        doNothing().when(handler).crearPropietario(any(CrearPropietarioDto.class));

        // Act & Assert
        mockMvc.perform(post("/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("PROPIETARIO_CREADO"))
                .andExpect(jsonPath("$.mensaje").value("El propietario ha sido creado exitosamente"))
                .andExpect(jsonPath("$.datos.correo").value(requestDto.getCorreo()))
                .andExpect(jsonPath("$.datos.nombre").value(requestDto.getNombre() + " " + requestDto.getApellido()))
                .andExpect(jsonPath("$.datos.documento").value(requestDto.getDocumento()))
                .andExpect(jsonPath("$.status").value(201));

        verify(restMapper, times(1)).toApplicationDto(any(CrearPropietarioRequestDto.class));
        verify(handler, times(1)).crearPropietario(any(CrearPropietarioDto.class));
    }

    @Test
    void deberiaRetornar400CuandoNombreEsNulo() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setNombre(null);

        // Act & Assert
        mockMvc.perform(post("/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
                .andExpect(jsonPath("$.mensaje").value("Los datos enviados no cumplen con las validaciones requeridas"))
                .andExpect(jsonPath("$.errores.nombre").exists());

        verify(handler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoNombreEsVacio() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setNombre("");

        // Act & Assert
        mockMvc.perform(post("/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").exists());

        verify(handler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoCorreoEsInvalido() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setCorreo("correo-invalido");

        // Act & Assert
        mockMvc.perform(post("/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.correo").exists());

        verify(handler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoDocumentoContieneLetras() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setDocumento("ABC123");

        // Act & Assert
        mockMvc.perform(post("/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.documento").exists());

        verify(handler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoCelularEsInvalido() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setCelular("celular-invalido");

        // Act & Assert
        mockMvc.perform(post("/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.celular").exists());

        verify(handler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoFechaNacimientoEsFutura() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setFechaNacimiento(LocalDate.now().plusDays(1));

        // Act & Assert
        mockMvc.perform(post("/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.fechaNacimiento").exists());

        verify(handler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoClaveEsMuyCorta() throws Exception {
        // Arrange
        CrearPropietarioRequestDto dto = crearRequestDtoValido();
        dto.setClave("12345"); // Menos de 6 caracteres

        // Act & Assert
        mockMvc.perform(post("/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.clave").exists());

        verify(handler, never()).crearPropietario(any());
    }

    @Test
    void deberiaRetornar400CuandoHandlerLanzaCampoInvalidoException() throws Exception {
        // Arrange
        CrearPropietarioRequestDto requestDto = crearRequestDtoValido();
        CrearPropietarioDto applicationDto = new CrearPropietarioDto();

        when(restMapper.toApplicationDto(any(CrearPropietarioRequestDto.class))).thenReturn(applicationDto);
        doThrow(new CampoInvalidoException("Documento inválido"))
                .when(handler).crearPropietario(any(CrearPropietarioDto.class));

        // Act & Assert
        mockMvc.perform(post("/usuarios/propietario")
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

        when(restMapper.toApplicationDto(any(CrearPropietarioRequestDto.class))).thenReturn(applicationDto);
        doThrow(new EmailInvalidoException())
                .when(handler).crearPropietario(any(CrearPropietarioDto.class));

        // Act & Assert
        mockMvc.perform(post("/usuarios/propietario")
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

        when(restMapper.toApplicationDto(any(CrearPropietarioRequestDto.class))).thenReturn(applicationDto);
        doThrow(new UsuarioMayorDeEdadException())
                .when(handler).crearPropietario(any(CrearPropietarioDto.class));

        // Act & Assert
        mockMvc.perform(post("/usuarios/propietario")
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
        mockMvc.perform(post("/usuarios/propietario")
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

        verify(handler, never()).crearPropietario(any());
    }

    // Metodo auxiliar
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
}