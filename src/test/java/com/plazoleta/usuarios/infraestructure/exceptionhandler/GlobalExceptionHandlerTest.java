package com.plazoleta.usuarios.infraestructure.exceptionhandler;

import com.plazoleta.usuarios.domain.exception.CampoInvalidoException;
import com.plazoleta.usuarios.domain.exception.EmailInvalidoException;
import com.plazoleta.usuarios.domain.exception.UsuarioMayorDeEdadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void deberiaManejaCampoInvalidoException() {
        // Arrange
        CampoInvalidoException exception = new CampoInvalidoException("Documento inválido");

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleCampoInvalido(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CAMPO_INVALIDO", response.getBody().get("codigo"));
        assertEquals("Documento inválido", response.getBody().get("mensaje"));
        assertEquals(400, response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void deberiaManejarEmailInvalidoException() {
        // Arrange
        EmailInvalidoException exception = new EmailInvalidoException();

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleEmailInvalido(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("EMAIL_INVALIDO", response.getBody().get("codigo"));
        assertEquals("El correo electrónico no tiene un formato válido", response.getBody().get("mensaje"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void deberiaManejarUsuarioMayorDeEdadException() {
        // Arrange
        UsuarioMayorDeEdadException exception = new UsuarioMayorDeEdadException();

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleUsuarioMayorDeEdad(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("EDAD_INSUFICIENTE", response.getBody().get("codigo"));
        assertTrue(response.getBody().get("mensaje").toString().contains("mayor"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void deberiaManejarMethodArgumentNotValidException() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("objeto", "nombre", "El nombre es obligatorio");
        FieldError fieldError2 = new FieldError("objeto", "correo", "El correo es inválido");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationExceptions(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDACION_FALLIDA", response.getBody().get("codigo"));
        assertEquals("Los datos enviados no cumplen con las validaciones requeridas", response.getBody().get("mensaje"));

        @SuppressWarnings("unchecked")
        Map<String, String> errores = (Map<String, String>) response.getBody().get("errores");
        assertEquals(2, errores.size());
        assertEquals("El nombre es obligatorio", errores.get("nombre"));
        assertEquals("El correo es inválido", errores.get("correo"));
    }

    @ParameterizedTest
    @MethodSource("providerErroresCampoMuyLargo")
    void deberiaManejarDataIntegrityViolationException_CampoMuyLargo(
            String mensajeError, String mensajeEsperado, String campoEsperado) {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException(mensajeError);

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDataIntegrityViolation(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ERROR_BASE_DATOS", response.getBody().get("codigo"));
        assertEquals(mensajeEsperado, response.getBody().get("mensaje"));
        assertEquals(campoEsperado, response.getBody().get("campo"));
        assertNotNull(response.getBody().get("detalle"));
    }

    private static Stream<Arguments> providerErroresCampoMuyLargo() {
        return Stream.of(
                Arguments.of(
                        "ERROR: el valor es demasiado largo para el tipo character varying(13) [phone]",
                        "El número de celular no puede tener más de 13 caracteres",
                        "celular"
                ),
                Arguments.of(
                        "ERROR: el valor es demasiado largo para el tipo character varying [document_number]",
                        "El número de documento excede la longitud máxima permitida",
                        "documento"
                ),
                Arguments.of(
                        "ERROR: el valor es demasiado largo para el tipo character varying [email]",
                        "El correo electrónico excede la longitud máxima permitida",
                        "correo"
                ),
                Arguments.of(
                        "ERROR: el valor es demasiado largo para el tipo character varying [first_name]",
                        "El nombre excede la longitud máxima permitida",
                        "nombre"
                ),
                Arguments.of(
                        "ERROR: el valor es demasiado largo para el tipo character varying [last_name]",
                        "El apellido excede la longitud máxima permitida",
                        "apellido"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("providerErroresDataIntegrity")
    void deberiaManejarDataIntegrityViolationException_Varios(
            String mensajeError, String mensajeEsperado, String campoEsperado) {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException(mensajeError);

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDataIntegrityViolation(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ERROR_BASE_DATOS", response.getBody().get("codigo"));
        assertEquals(mensajeEsperado, response.getBody().get("mensaje"));
        assertEquals(campoEsperado, response.getBody().get("campo"));
    }

    private static Stream<Arguments> providerErroresDataIntegrity() {
        return Stream.of(
                Arguments.of(
                        "ERROR: duplicate key value violates unique constraint [email]",
                        "Ya existe un usuario con este correo electrónico",
                        "correo"
                ),
                Arguments.of(
                        "ERROR: duplicate key value violates unique constraint [document]",
                        "Ya existe un usuario con este número de documento",
                        "documento"
                ),
                Arguments.of(
                        "ERROR: algún error desconocido",
                        "Error al guardar los datos en la base de datos",
                        "desconocido"
                )
        );
    }

    @Test
    void deberiaManejarRuntimeException() {
        // Arrange
        RuntimeException exception = new RuntimeException("Error inesperado");

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRuntimeException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ERROR_INTERNO", response.getBody().get("codigo"));
        assertEquals("Ha ocurrido un error inesperado. Por favor, contacta al administrador", response.getBody().get("mensaje"));
        assertEquals("Error inesperado", response.getBody().get("detalleInterno"));
        assertEquals(500, response.getBody().get("status"));
    }

    @Test
    void deberiaIncluirTimestampEnTodasLasRespuestas() {
        // Arrange
        CampoInvalidoException exception = new CampoInvalidoException("Test");

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleCampoInvalido(exception);

        // Assert
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().get("timestamp"));
        assertTrue(response.getBody().get("timestamp") instanceof String);
    }
}