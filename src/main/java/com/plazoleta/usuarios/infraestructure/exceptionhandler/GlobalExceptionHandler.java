package com.plazoleta.usuarios.infraestructure.exceptionhandler;

import com.plazoleta.usuarios.domain.exception.CampoInvalidoException;
import com.plazoleta.usuarios.domain.exception.EmailInvalidoException;
import com.plazoleta.usuarios.domain.exception.UsuarioMayorDeEdadException;
import org.springframework.dao.DataIntegrityViolationException;
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

    // Constantes para claves de respuesta
    private static final String KEY_CODIGO = "codigo";
    private static final String KEY_MENSAJE = "mensaje";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_STATUS = "status";
    private static final String KEY_CAMPO = "campo";
    private static final String KEY_MENSAJE_CLARO = "mensajeClaro";

    @ExceptionHandler(CampoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleCampoInvalido(CampoInvalidoException ex) {
        Map<String, Object> response = createErrorResponse(
                "CAMPO_INVALIDO",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EmailInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailInvalido(EmailInvalidoException ex) {
        Map<String, Object> response = createErrorResponse(
                "EMAIL_INVALIDO",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UsuarioMayorDeEdadException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioMayorDeEdad(UsuarioMayorDeEdadException ex) {
        Map<String, Object> response = createErrorResponse(
                "EDAD_INSUFICIENTE",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String mensaje = ex.getMessage();
        Map<String, String> errorInfo = parseDataIntegrityError(mensaje);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(KEY_CODIGO, "ERROR_BASE_DATOS");
        response.put(KEY_MENSAJE, errorInfo.get(KEY_MENSAJE_CLARO));
        response.put(KEY_CAMPO, errorInfo.get(KEY_CAMPO));
        response.put("detalle", "Verifica que los datos cumplan con las restricciones de tamaño y formato");
        response.put(KEY_TIMESTAMP, LocalDateTime.now().toString());
        response.put(KEY_STATUS, HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private Map<String, String> parseDataIntegrityError(String mensaje) {
        Map<String, String> errorInfo = new HashMap<>();
        errorInfo.put(KEY_MENSAJE_CLARO, "Error al guardar los datos en la base de datos");
        errorInfo.put(KEY_CAMPO, "desconocido");

        if (mensaje == null) {
            return errorInfo;
        }

        if (isUniqueConstraintViolation(mensaje)) {
            parseUniqueConstraintError(mensaje, errorInfo);
        } else if (isValueTooLongError(mensaje)) {
            parseValueTooLongError(mensaje, errorInfo);
        } else if (isNotNullViolation(mensaje)) {
            parseNotNullError(errorInfo);
        }

        return errorInfo;
    }

    private boolean isUniqueConstraintViolation(String mensaje) {
        return mensaje.contains("unique constraint") || mensaje.contains("duplicate key");
    }

    private boolean isValueTooLongError(String mensaje) {
        return mensaje.contains("demasiado largo") || mensaje.contains("value too long");
    }

    private boolean isNotNullViolation(String mensaje) {
        return mensaje.contains("not-null") || mensaje.contains("null value");
    }

    private void parseUniqueConstraintError(String mensaje, Map<String, String> errorInfo) {
        errorInfo.put(KEY_MENSAJE_CLARO, "Ya existe un registro con estos datos");
        errorInfo.put(KEY_CAMPO, "datos");

        if (mensaje.contains("email")) {
            errorInfo.put(KEY_CAMPO, "correo");
            errorInfo.put(KEY_MENSAJE_CLARO, "Ya existe un usuario con este correo electrónico");
        } else if (mensaje.contains("document")) {
            errorInfo.put(KEY_CAMPO, "documento");
            errorInfo.put(KEY_MENSAJE_CLARO, "Ya existe un usuario con este número de documento");
        }
    }

    private void parseValueTooLongError(String mensaje, Map<String, String> errorInfo) {
        if (mensaje.contains("phone") || mensaje.contains("varying(13)")) {
            errorInfo.put(KEY_MENSAJE_CLARO, "El número de celular no puede tener más de 13 caracteres");
            errorInfo.put(KEY_CAMPO, "celular");
        } else if (mensaje.contains("document_number")) {
            errorInfo.put(KEY_MENSAJE_CLARO, "El número de documento excede la longitud máxima permitida");
            errorInfo.put(KEY_CAMPO, "documento");
        } else if (mensaje.contains("email")) {
            errorInfo.put(KEY_MENSAJE_CLARO, "El correo electrónico excede la longitud máxima permitida");
            errorInfo.put(KEY_CAMPO, "correo");
        } else if (mensaje.contains("first_name")) {
            errorInfo.put(KEY_MENSAJE_CLARO, "El nombre excede la longitud máxima permitida");
            errorInfo.put(KEY_CAMPO, "nombre");
        } else if (mensaje.contains("last_name")) {
            errorInfo.put(KEY_MENSAJE_CLARO, "El apellido excede la longitud máxima permitida");
            errorInfo.put(KEY_CAMPO, "apellido");
        } else if (mensaje.contains("password")) {
            errorInfo.put(KEY_MENSAJE_CLARO, "La contraseña excede la longitud máxima permitida");
            errorInfo.put(KEY_CAMPO, "clave");
        }
    }

    private void parseNotNullError(Map<String, String> errorInfo) {
        errorInfo.put(KEY_MENSAJE_CLARO, "Falta un campo obligatorio");
        errorInfo.put(KEY_CAMPO, "desconocido");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = createErrorResponse(
                "ERROR_INTERNO",
                "Ha ocurrido un error inesperado. Por favor, contacta al administrador",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        response.put("detalleInterno", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
