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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String KEY_CODE = "code";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_STATUS = "status";
    private static final String KEY_FIELD = "field";
    private static final String KEY_ERRORS = "errors";

    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidField(InvalidFieldException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("INVALID_FIELD", ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidEmail(InvalidEmailException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("INVALID_EMAIL", ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(UserUnderAgeException.class)
    public ResponseEntity<Map<String, Object>> handleUserUnderAge(UserUnderAgeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("INSUFFICIENT_AGE", ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRoleNotFound(RoleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("ROLE_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyRegistered(EmailAlreadyRegisteredException ex) {
        Map<String, Object> response = createErrorResponse("EMAIL_ALREADY_REGISTERED", ex.getMessage(), HttpStatus.CONFLICT);
        response.put(KEY_FIELD, "email");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DocumentAlreadyRegisteredException.class)
    public ResponseEntity<Map<String, Object>> handleDocumentAlreadyRegistered(DocumentAlreadyRegisteredException ex) {
        Map<String, Object> response = createErrorResponse("DOCUMENT_ALREADY_REGISTERED", ex.getMessage(), HttpStatus.CONFLICT);
        response.put(KEY_FIELD, "documentNumber");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ValueExceedsLengthException.class)
    public ResponseEntity<Map<String, Object>> handleValueExceedsLength(ValueExceedsLengthException ex) {
        Map<String, Object> response = createErrorResponse("VALUE_EXCEEDS_LENGTH", ex.getMessage(), HttpStatus.BAD_REQUEST);
        response.put(KEY_FIELD, ex.getField());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("INVALID_CREDENTIALS", ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(RequiredFieldException.class)
    public ResponseEntity<Map<String, Object>> handleRequiredField(RequiredFieldException ex) {
        Map<String, Object> response = createErrorResponse("REQUIRED_FIELD", ex.getMessage(), HttpStatus.BAD_REQUEST);
        response.put(KEY_FIELD, "unknown");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(KEY_CODE, "VALIDATION_FAILED");
        response.put(KEY_MESSAGE, "Submitted data does not meet required validations");
        response.put(KEY_ERRORS, errors);
        response.put(KEY_TIMESTAMP, LocalDateTime.now().toString());
        response.put(KEY_STATUS, HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private Map<String, Object> createErrorResponse(String code, String message, HttpStatus status) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(KEY_CODE, code);
        response.put(KEY_MESSAGE, message);
        response.put(KEY_TIMESTAMP, LocalDateTime.now().toString());
        response.put(KEY_STATUS, status.value());
        return response;
    }
}