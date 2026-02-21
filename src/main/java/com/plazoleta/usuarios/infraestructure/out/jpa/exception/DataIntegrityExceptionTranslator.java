package com.plazoleta.usuarios.infraestructure.out.jpa.exception;

import com.plazoleta.usuarios.domain.exception.RequiredFieldException;
import com.plazoleta.usuarios.domain.exception.EmailAlreadyRegisteredException;
import com.plazoleta.usuarios.domain.exception.DocumentAlreadyRegisteredException;
import com.plazoleta.usuarios.domain.exception.ValueExceedsLengthException;
import org.springframework.dao.DataIntegrityViolationException;

public final class DataIntegrityExceptionTranslator {

    private DataIntegrityExceptionTranslator() {
    }

    public static void throwSpecific(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        if (message == null) {
            throw new RequiredFieldException("Error saving data to database");
        }

        if (isUniqueConstraintViolation(message)) {
            throwUniqueConstraint(message);
        } else if (isValueTooLongError(message)) {
            throwValueTooLong(message);
        } else if (isNotNullViolation(message)) {
            throw new RequiredFieldException();
        }

        throw new RequiredFieldException("Error saving data to database");
    }

    private static boolean isUniqueConstraintViolation(String message) {
        return message.contains("unique constraint") || message.contains("duplicate key");
    }

    private static boolean isValueTooLongError(String message) {
        return message.contains("value too long") || message.contains("demasiado largo");
    }

    private static boolean isNotNullViolation(String message) {
        return message.contains("not-null") || message.contains("null value");
    }

    private static void throwUniqueConstraint(String message) {
        if (message.contains("email")) {
            throw new EmailAlreadyRegisteredException();
        }
        if (message.contains("document")) {
            throw new DocumentAlreadyRegisteredException();
        }
        throw new EmailAlreadyRegisteredException("A record with this data already exists");
    }

    private static void throwValueTooLong(String message) {
        if (message.contains("phone") || message.contains("varying(13)")) {
            throw new ValueExceedsLengthException(
                    "Phone number cannot exceed 13 characters", "phone");
        }
        if (message.contains("document_number")) {
            throw new ValueExceedsLengthException(
                    "Document number exceeds maximum allowed length", "documentNumber");
        }
        if (message.contains("email")) {
            throw new ValueExceedsLengthException(
                    "Email exceeds maximum allowed length", "email");
        }
        if (message.contains("first_name")) {
            throw new ValueExceedsLengthException(
                    "First name exceeds maximum allowed length", "firstName");
        }
        if (message.contains("last_name")) {
            throw new ValueExceedsLengthException(
                    "Last name exceeds maximum allowed length", "lastName");
        }
        if (message.contains("password")) {
            throw new ValueExceedsLengthException(
                    "Password exceeds maximum allowed length", "password");
        }
        throw new ValueExceedsLengthException("Value exceeds maximum allowed length", "unknown");
    }
}