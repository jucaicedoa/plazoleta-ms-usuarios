package com.plazoleta.usuarios.infraestructure.exceptionhandler;

import com.plazoleta.usuarios.domain.exception.CredencialesInvalidasException;
import com.plazoleta.usuarios.domain.exception.CampoInvalidoException;
import com.plazoleta.usuarios.domain.exception.CampoObligatorioException;
import com.plazoleta.usuarios.domain.exception.CorreoYaRegistradoException;
import com.plazoleta.usuarios.domain.exception.DocumentoYaRegistradoException;
import com.plazoleta.usuarios.domain.exception.EmailInvalidoException;
import com.plazoleta.usuarios.domain.exception.RolNoEncontradoException;
import com.plazoleta.usuarios.domain.exception.UsuarioMayorDeEdadException;
import com.plazoleta.usuarios.domain.exception.ValorExcedeLongitudException;
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

/**
 * Manejador de excepciones que solo trata excepciones específicas del dominio y de validación.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String KEY_CODIGO = "codigo";
    private static final String KEY_MENSAJE = "mensaje";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_STATUS = "status";
    private static final String KEY_CAMPO = "campo";

    @ExceptionHandler(CampoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleCampoInvalido(CampoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("CAMPO_INVALIDO", ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(EmailInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailInvalido(EmailInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("EMAIL_INVALIDO", ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(UsuarioMayorDeEdadException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioMayorDeEdad(UsuarioMayorDeEdadException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("EDAD_INSUFICIENTE", ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(RolNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleRolNoEncontrado(RolNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("ROL_NO_ENCONTRADO", ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(CorreoYaRegistradoException.class)
    public ResponseEntity<Map<String, Object>> handleCorreoYaRegistrado(CorreoYaRegistradoException ex) {
        Map<String, Object> response = createErrorResponse("CORREO_YA_REGISTRADO", ex.getMessage(), HttpStatus.CONFLICT);
        response.put(KEY_CAMPO, "correo");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DocumentoYaRegistradoException.class)
    public ResponseEntity<Map<String, Object>> handleDocumentoYaRegistrado(DocumentoYaRegistradoException ex) {
        Map<String, Object> response = createErrorResponse("DOCUMENTO_YA_REGISTRADO", ex.getMessage(), HttpStatus.CONFLICT);
        response.put(KEY_CAMPO, "documento");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ValorExcedeLongitudException.class)
    public ResponseEntity<Map<String, Object>> handleValorExcedeLongitud(ValorExcedeLongitudException ex) {
        Map<String, Object> response = createErrorResponse("VALOR_EXCEDE_LONGITUD", ex.getMessage(), HttpStatus.BAD_REQUEST);
        response.put(KEY_CAMPO, ex.getCampo());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("CREDENCIALES_INVALIDAS", ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(CampoObligatorioException.class)
    public ResponseEntity<Map<String, Object>> handleCampoObligatorio(CampoObligatorioException ex) {
        Map<String, Object> response = createErrorResponse("CAMPO_OBLIGATORIO", ex.getMessage(), HttpStatus.BAD_REQUEST);
        response.put(KEY_CAMPO, "desconocido");
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
        response.put(KEY_CODIGO, "VALIDACION_FALLIDA");
        response.put(KEY_MENSAJE, "Los datos enviados no cumplen con las validaciones requeridas");
        response.put("errores", errors);
        response.put(KEY_TIMESTAMP, LocalDateTime.now().toString());
        response.put(KEY_STATUS, HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private Map<String, Object> createErrorResponse(String codigo, String mensaje, HttpStatus status) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(KEY_CODIGO, codigo);
        response.put(KEY_MENSAJE, mensaje);
        response.put(KEY_TIMESTAMP, LocalDateTime.now().toString());
        response.put(KEY_STATUS, status.value());
        return response;
    }
}