package com.plazoleta.usuarios.infraestructure.out.jpa.exception;

import com.plazoleta.usuarios.domain.exception.RequiredFieldException;
import com.plazoleta.usuarios.domain.exception.EmailAlreadyRegisteredException;
import com.plazoleta.usuarios.domain.exception.DocumentAlreadyRegisteredException;
import com.plazoleta.usuarios.domain.exception.ValueExceedsLengthException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataIntegrityExceptionTranslatorTest {

    @Test
    void deberiaLanzarEmailAlreadyRegisteredExceptionCuandoHayUniqueConstraintEnEmail() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: duplicate key value violates unique constraint [email]");

        // Act & Assert
        EmailAlreadyRegisteredException exception = assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("A user with this email already exists", exception.getMessage());
    }

    @Test
    void deberiaLanzarEmailAlreadyRegisteredExceptionCuandoHayDuplicateKeyEnEmail() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "duplicate key value violates unique constraint [email]");

        // Act & Assert
        EmailAlreadyRegisteredException exception = assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("A user with this email already exists", exception.getMessage());
    }

    @Test
    void deberiaLanzarEmailAlreadyRegisteredExceptionConMensajeCustomCuandoUniqueConstraintNoEsEmailNiDocument() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "duplicate key value violates unique constraint [otro_campo]");

        // Act & Assert
        EmailAlreadyRegisteredException exception = assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("A record with this data already exists", exception.getMessage());
    }

    @Test
    void deberiaLanzarDocumentAlreadyRegisteredExceptionCuandoHayUniqueConstraintEnDocument() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: duplicate key value violates unique constraint [document]");

        // Act & Assert
        DocumentAlreadyRegisteredException exception = assertThrows(
                DocumentAlreadyRegisteredException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("A user with this document number already exists", exception.getMessage());
    }

    @Test
    void deberiaLanzarValueExceedsLengthExceptionParaCelular() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: el valor es demasiado largo para el tipo character varying(13) [phone]");

        // Act & Assert
        ValueExceedsLengthException exception = assertThrows(
                ValueExceedsLengthException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Phone number cannot exceed 13 characters", exception.getMessage());
        assertEquals("phone", exception.getField());
    }

    @Test
    void deberiaLanzarValueExceedsLengthExceptionParaCelularConVarying13() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying(13)");

        // Act & Assert
        ValueExceedsLengthException exception = assertThrows(
                ValueExceedsLengthException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("phone", exception.getField());
    }

    @Test
    void deberiaLanzarValueExceedsLengthExceptionParaDocumento() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying [document_number]");

        // Act & Assert
        ValueExceedsLengthException exception = assertThrows(
                ValueExceedsLengthException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Document number exceeds maximum allowed length", exception.getMessage());
        assertEquals("documentNumber", exception.getField());
    }

    @Test
    void deberiaLanzarValueExceedsLengthExceptionParaEmail() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying [email]");

        // Act & Assert
        ValueExceedsLengthException exception = assertThrows(
                ValueExceedsLengthException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Email exceeds maximum allowed length", exception.getMessage());
        assertEquals("email", exception.getField());
    }

    @Test
    void deberiaLanzarValueExceedsLengthExceptionParaNombre() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying [first_name]");

        // Act & Assert
        ValueExceedsLengthException exception = assertThrows(
                ValueExceedsLengthException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("First name exceeds maximum allowed length", exception.getMessage());
        assertEquals("firstName", exception.getField());
    }

    @Test
    void deberiaLanzarValueExceedsLengthExceptionParaApellido() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying [last_name]");

        // Act & Assert
        ValueExceedsLengthException exception = assertThrows(
                ValueExceedsLengthException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Last name exceeds maximum allowed length", exception.getMessage());
        assertEquals("lastName", exception.getField());
    }

    @Test
    void deberiaLanzarValueExceedsLengthExceptionParaPassword() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying [password]");

        // Act & Assert
        ValueExceedsLengthException exception = assertThrows(
                ValueExceedsLengthException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Password exceeds maximum allowed length", exception.getMessage());
        assertEquals("password", exception.getField());
    }

    @Test
    void deberiaLanzarValueExceedsLengthExceptionGenericoCuandoNoSeIdentificaElCampo() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying [campo_desconocido]");

        // Act & Assert
        ValueExceedsLengthException exception = assertThrows(
                ValueExceedsLengthException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Value exceeds maximum allowed length", exception.getMessage());
        assertEquals("unknown", exception.getField());
    }

    @Test
    void deberiaLanzarRequiredFieldExceptionCuandoHayNotNullViolation() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: not-null constraint violation");

        // Act & Assert
        RequiredFieldException exception = assertThrows(
                RequiredFieldException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Required field is missing", exception.getMessage());
    }

    @Test
    void deberiaLanzarRequiredFieldExceptionCuandoHayNullValue() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: null value in column");

        // Act & Assert
        RequiredFieldException exception = assertThrows(
                RequiredFieldException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Required field is missing", exception.getMessage());
    }

    @Test
    void deberiaLanzarRequiredFieldExceptionCuandoMensajeEsNull() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(null);

        // Act & Assert
        RequiredFieldException exception = assertThrows(
                RequiredFieldException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Error saving data to database", exception.getMessage());
    }

    @Test
    void deberiaLanzarRequiredFieldExceptionCuandoErrorNoEsReconocido() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: algún error desconocido de base de datos");

        // Act & Assert
        RequiredFieldException exception = assertThrows(
                RequiredFieldException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Error saving data to database", exception.getMessage());
    }
}