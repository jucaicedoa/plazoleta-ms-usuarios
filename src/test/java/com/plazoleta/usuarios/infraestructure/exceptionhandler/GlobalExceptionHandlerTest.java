package com.plazoleta.usuarios.infraestructure.exceptionhandler;

import com.plazoleta.usuarios.domain.exception.CampoInvalidoException;
import com.plazoleta.usuarios.domain.exception.CampoObligatorioException;
import com.plazoleta.usuarios.domain.exception.CorreoYaRegistradoException;
import com.plazoleta.usuarios.domain.exception.DocumentoYaRegistradoException;
import com.plazoleta.usuarios.domain.exception.EmailInvalidoException;
import com.plazoleta.usuarios.domain.exception.RolNoEncontradoException;
import com.plazoleta.usuarios.domain.exception.UsuarioMayorDeEdadException;
import com.plazoleta.usuarios.domain.exception.ValorExcedeLongitudException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

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
    void deberiaManejarRolNoEncontradoException() {
        // Arrange
        RolNoEncontradoException exception = new RolNoEncontradoException("Rol PROPIETARIO no encontrado en la base de datos");

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRolNoEncontrado(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ROL_NO_ENCONTRADO", response.getBody().get("codigo"));
        assertEquals("Rol PROPIETARIO no encontrado en la base de datos", response.getBody().get("mensaje"));
        assertEquals(404, response.getBody().get("status"));
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

    @Test
    void deberiaManejarCorreoYaRegistradoException() {
        CorreoYaRegistradoException exception = new CorreoYaRegistradoException();

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleCorreoYaRegistrado(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CORREO_YA_REGISTRADO", response.getBody().get("codigo"));
        assertEquals("Ya existe un usuario con este correo electrónico", response.getBody().get("mensaje"));
        assertEquals("correo", response.getBody().get("campo"));
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    void deberiaManejarCorreoYaRegistradoExceptionConMensajePersonalizado() {
        String mensajePersonalizado = "Ya existe un registro con estos datos";
        CorreoYaRegistradoException exception = new CorreoYaRegistradoException(mensajePersonalizado);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleCorreoYaRegistrado(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CORREO_YA_REGISTRADO", response.getBody().get("codigo"));
        assertEquals(mensajePersonalizado, response.getBody().get("mensaje"));
        assertEquals("correo", response.getBody().get("campo"));
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    void deberiaManejarDocumentoYaRegistradoException() {
        DocumentoYaRegistradoException exception = new DocumentoYaRegistradoException();

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDocumentoYaRegistrado(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DOCUMENTO_YA_REGISTRADO", response.getBody().get("codigo"));
        assertEquals("Ya existe un usuario con este número de documento", response.getBody().get("mensaje"));
        assertEquals("documento", response.getBody().get("campo"));
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    void deberiaManejarDocumentoYaRegistradoExceptionConMensajePersonalizado() {
        String mensajePersonalizado = "El documento ya está registrado en el sistema";
        DocumentoYaRegistradoException exception = new DocumentoYaRegistradoException(mensajePersonalizado);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDocumentoYaRegistrado(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DOCUMENTO_YA_REGISTRADO", response.getBody().get("codigo"));
        assertEquals(mensajePersonalizado, response.getBody().get("mensaje"));
        assertEquals("documento", response.getBody().get("campo"));
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    void deberiaManejarValorExcedeLongitudException() {
        ValorExcedeLongitudException exception = new ValorExcedeLongitudException(
                "El número de celular no puede tener más de 13 caracteres", "celular");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValorExcedeLongitud(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALOR_EXCEDE_LONGITUD", response.getBody().get("codigo"));
        assertEquals("El número de celular no puede tener más de 13 caracteres", response.getBody().get("mensaje"));
        assertEquals("celular", response.getBody().get("campo"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void deberiaManejarValorExcedeLongitudExceptionConCampoNull() {
        ValorExcedeLongitudException exception = new ValorExcedeLongitudException(
                "El valor excede la longitud máxima permitida", null);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValorExcedeLongitud(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALOR_EXCEDE_LONGITUD", response.getBody().get("codigo"));
        assertEquals("El valor excede la longitud máxima permitida", response.getBody().get("mensaje"));
        assertEquals("desconocido", response.getBody().get("campo"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void deberiaManejarCampoObligatorioException() {
        CampoObligatorioException exception = new CampoObligatorioException();

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleCampoObligatorio(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CAMPO_OBLIGATORIO", response.getBody().get("codigo"));
        assertEquals("Falta un campo obligatorio", response.getBody().get("mensaje"));
        assertEquals("desconocido", response.getBody().get("campo"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void deberiaManejarCampoObligatorioExceptionConMensajePersonalizado() {
        String mensajePersonalizado = "Error al guardar los datos en la base de datos";
        CampoObligatorioException exception = new CampoObligatorioException(mensajePersonalizado);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleCampoObligatorio(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CAMPO_OBLIGATORIO", response.getBody().get("codigo"));
        assertEquals(mensajePersonalizado, response.getBody().get("mensaje"));
        assertEquals("desconocido", response.getBody().get("campo"));
        assertEquals(400, response.getBody().get("status"));
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