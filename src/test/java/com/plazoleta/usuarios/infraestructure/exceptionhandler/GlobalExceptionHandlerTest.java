package com.plazoleta.usuarios.infraestructure.exceptionhandler;

import com.plazoleta.usuarios.domain.exception.InvalidCredentialsException;
import com.plazoleta.usuarios.domain.exception.InvalidFieldException;
import com.plazoleta.usuarios.domain.exception.RequiredFieldException;
import com.plazoleta.usuarios.domain.exception.EmailAlreadyRegisteredException;
import com.plazoleta.usuarios.domain.exception.DocumentAlreadyRegisteredException;
import com.plazoleta.usuarios.domain.exception.InvalidEmailException;
import com.plazoleta.usuarios.domain.exception.RoleNotFoundException;
import com.plazoleta.usuarios.domain.exception.UserUnderAgeException;
import com.plazoleta.usuarios.domain.exception.ValueExceedsLengthException;
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
    void shouldHandleInvalidCredentialsException() {
        InvalidCredentialsException exception = new InvalidCredentialsException("Invalid credentials");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleInvalidCredentials(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_CREDENTIALS", response.getBody().get("code"));
        assertEquals("Invalid credentials", response.getBody().get("message"));
        assertEquals(401, response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void shouldHandleInvalidFieldException() {
        InvalidFieldException exception = new InvalidFieldException("Invalid document");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleInvalidField(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_FIELD", response.getBody().get("code"));
        assertEquals("Invalid document", response.getBody().get("message"));
        assertEquals(400, response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void shouldHandleInvalidEmailException() {
        InvalidEmailException exception = new InvalidEmailException();

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleInvalidEmail(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_EMAIL", response.getBody().get("code"));
        assertEquals("Email format is invalid", response.getBody().get("message"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void shouldHandleUserUnderAgeException() {
        UserUnderAgeException exception = new UserUnderAgeException();

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleUserUnderAge(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INSUFFICIENT_AGE", response.getBody().get("code"));
        assertTrue(response.getBody().get("message").toString().contains("18"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void shouldHandleRoleNotFoundException() {
        RoleNotFoundException exception = new RoleNotFoundException("Role PROPIETARIO not found in database");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRoleNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ROLE_NOT_FOUND", response.getBody().get("code"));
        assertEquals("Role PROPIETARIO not found in database", response.getBody().get("message"));
        assertEquals(404, response.getBody().get("status"));
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("obj", "firstName", "First name is required");
        FieldError fieldError2 = new FieldError("obj", "email", "Invalid email");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_FAILED", response.getBody().get("code"));
        assertEquals("Submitted data does not meet required validations", response.getBody().get("message"));

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals(2, errors.size());
        assertEquals("First name is required", errors.get("firstName"));
        assertEquals("Invalid email", errors.get("email"));
    }

    @Test
    void shouldHandleEmailAlreadyRegisteredException() {
        EmailAlreadyRegisteredException exception = new EmailAlreadyRegisteredException();

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleEmailAlreadyRegistered(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("EMAIL_ALREADY_REGISTERED", response.getBody().get("code"));
        assertEquals("A user with this email already exists", response.getBody().get("message"));
        assertEquals("email", response.getBody().get("field"));
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    void shouldHandleEmailAlreadyRegisteredExceptionWithCustomMessage() {
        String customMessage = "A record with this data already exists";
        EmailAlreadyRegisteredException exception = new EmailAlreadyRegisteredException(customMessage);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleEmailAlreadyRegistered(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("EMAIL_ALREADY_REGISTERED", response.getBody().get("code"));
        assertEquals(customMessage, response.getBody().get("message"));
        assertEquals("email", response.getBody().get("field"));
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    void shouldHandleDocumentAlreadyRegisteredException() {
        DocumentAlreadyRegisteredException exception = new DocumentAlreadyRegisteredException();

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDocumentAlreadyRegistered(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DOCUMENT_ALREADY_REGISTERED", response.getBody().get("code"));
        assertEquals("A user with this document number already exists", response.getBody().get("message"));
        assertEquals("documentNumber", response.getBody().get("field"));
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    void shouldHandleDocumentAlreadyRegisteredExceptionWithCustomMessage() {
        String customMessage = "Document already registered in system";
        DocumentAlreadyRegisteredException exception = new DocumentAlreadyRegisteredException(customMessage);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDocumentAlreadyRegistered(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DOCUMENT_ALREADY_REGISTERED", response.getBody().get("code"));
        assertEquals(customMessage, response.getBody().get("message"));
        assertEquals("documentNumber", response.getBody().get("field"));
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    void shouldHandleValueExceedsLengthException() {
        ValueExceedsLengthException exception = new ValueExceedsLengthException(
                "Phone number cannot exceed 13 characters", "phone");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValueExceedsLength(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALUE_EXCEEDS_LENGTH", response.getBody().get("code"));
        assertEquals("Phone number cannot exceed 13 characters", response.getBody().get("message"));
        assertEquals("phone", response.getBody().get("field"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void shouldHandleValueExceedsLengthExceptionWithNullField() {
        ValueExceedsLengthException exception = new ValueExceedsLengthException(
                "Value exceeds maximum allowed length", null);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValueExceedsLength(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALUE_EXCEEDS_LENGTH", response.getBody().get("code"));
        assertEquals("Value exceeds maximum allowed length", response.getBody().get("message"));
        assertEquals("unknown", response.getBody().get("field"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void shouldHandleRequiredFieldException() {
        RequiredFieldException exception = new RequiredFieldException();

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRequiredField(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("REQUIRED_FIELD", response.getBody().get("code"));
        assertEquals("Required field is missing", response.getBody().get("message"));
        assertEquals("unknown", response.getBody().get("field"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void shouldHandleRequiredFieldExceptionWithCustomMessage() {
        String customMessage = "Error saving data to database";
        RequiredFieldException exception = new RequiredFieldException(customMessage);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRequiredField(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("REQUIRED_FIELD", response.getBody().get("code"));
        assertEquals(customMessage, response.getBody().get("message"));
        assertEquals("unknown", response.getBody().get("field"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void shouldIncludeTimestampInAllResponses() {
        InvalidFieldException exception = new InvalidFieldException("Test");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleInvalidField(exception);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().get("timestamp"));
        assertTrue(response.getBody().get("timestamp") instanceof String);
    }
}