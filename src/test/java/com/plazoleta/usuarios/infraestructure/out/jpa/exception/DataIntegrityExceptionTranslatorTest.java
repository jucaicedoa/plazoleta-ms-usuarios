package com.plazoleta.usuarios.infraestructure.out.jpa.exception;

import com.plazoleta.usuarios.domain.exception.CampoObligatorioException;
import com.plazoleta.usuarios.domain.exception.CorreoYaRegistradoException;
import com.plazoleta.usuarios.domain.exception.DocumentoYaRegistradoException;
import com.plazoleta.usuarios.domain.exception.ValorExcedeLongitudException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataIntegrityExceptionTranslatorTest {

    @Test
    void deberiaLanzarCorreoYaRegistradoExceptionCuandoHayUniqueConstraintEnEmail() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: duplicate key value violates unique constraint [email]");

        // Act & Assert
        CorreoYaRegistradoException exception = assertThrows(
                CorreoYaRegistradoException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Ya existe un usuario con este correo electrónico", exception.getMessage());
    }

    @Test
    void deberiaLanzarCorreoYaRegistradoExceptionCuandoHayDuplicateKeyEnEmail() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "duplicate key value violates unique constraint [email]");

        // Act & Assert
        CorreoYaRegistradoException exception = assertThrows(
                CorreoYaRegistradoException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Ya existe un usuario con este correo electrónico", exception.getMessage());
    }

    @Test
    void deberiaLanzarCorreoYaRegistradoExceptionConMensajeCustomCuandoUniqueConstraintNoEsEmailNiDocument() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "duplicate key value violates unique constraint [otro_campo]");

        // Act & Assert
        CorreoYaRegistradoException exception = assertThrows(
                CorreoYaRegistradoException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Ya existe un registro con estos datos", exception.getMessage());
    }

    @Test
    void deberiaLanzarDocumentoYaRegistradoExceptionCuandoHayUniqueConstraintEnDocument() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: duplicate key value violates unique constraint [document]");

        // Act & Assert
        DocumentoYaRegistradoException exception = assertThrows(
                DocumentoYaRegistradoException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Ya existe un usuario con este número de documento", exception.getMessage());
    }

    @Test
    void deberiaLanzarValorExcedeLongitudExceptionParaCelular() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: el valor es demasiado largo para el tipo character varying(13) [phone]");

        // Act & Assert
        ValorExcedeLongitudException exception = assertThrows(
                ValorExcedeLongitudException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("El número de celular no puede tener más de 13 caracteres", exception.getMessage());
        assertEquals("celular", exception.getCampo());
    }

    @Test
    void deberiaLanzarValorExcedeLongitudExceptionParaCelularConVarying13() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying(13)");

        // Act & Assert
        ValorExcedeLongitudException exception = assertThrows(
                ValorExcedeLongitudException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("celular", exception.getCampo());
    }

    @Test
    void deberiaLanzarValorExcedeLongitudExceptionParaDocumento() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying [document_number]");

        // Act & Assert
        ValorExcedeLongitudException exception = assertThrows(
                ValorExcedeLongitudException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("El número de documento excede la longitud máxima permitida", exception.getMessage());
        assertEquals("documento", exception.getCampo());
    }

    @Test
    void deberiaLanzarValorExcedeLongitudExceptionParaEmail() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying [email]");

        // Act & Assert
        ValorExcedeLongitudException exception = assertThrows(
                ValorExcedeLongitudException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("El correo electrónico excede la longitud máxima permitida", exception.getMessage());
        assertEquals("correo", exception.getCampo());
    }

    @Test
    void deberiaLanzarValorExcedeLongitudExceptionParaNombre() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying [first_name]");

        // Act & Assert
        ValorExcedeLongitudException exception = assertThrows(
                ValorExcedeLongitudException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("El nombre excede la longitud máxima permitida", exception.getMessage());
        assertEquals("nombre", exception.getCampo());
    }

    @Test
    void deberiaLanzarValorExcedeLongitudExceptionParaApellido() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying [last_name]");

        // Act & Assert
        ValorExcedeLongitudException exception = assertThrows(
                ValorExcedeLongitudException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("El apellido excede la longitud máxima permitida", exception.getMessage());
        assertEquals("apellido", exception.getCampo());
    }

    @Test
    void deberiaLanzarValorExcedeLongitudExceptionParaPassword() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying [password]");

        // Act & Assert
        ValorExcedeLongitudException exception = assertThrows(
                ValorExcedeLongitudException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("La contraseña excede la longitud máxima permitida", exception.getMessage());
        assertEquals("clave", exception.getCampo());
    }

    @Test
    void deberiaLanzarValorExcedeLongitudExceptionGenericoCuandoNoSeIdentificaElCampo() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: value too long for type character varying [campo_desconocido]");

        // Act & Assert
        ValorExcedeLongitudException exception = assertThrows(
                ValorExcedeLongitudException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("El valor excede la longitud máxima permitida", exception.getMessage());
        assertEquals("desconocido", exception.getCampo());
    }

    @Test
    void deberiaLanzarCampoObligatorioExceptionCuandoHayNotNullViolation() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: not-null constraint violation");

        // Act & Assert
        CampoObligatorioException exception = assertThrows(
                CampoObligatorioException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Falta un campo obligatorio", exception.getMessage());
    }

    @Test
    void deberiaLanzarCampoObligatorioExceptionCuandoHayNullValue() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: null value in column");

        // Act & Assert
        CampoObligatorioException exception = assertThrows(
                CampoObligatorioException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Falta un campo obligatorio", exception.getMessage());
    }

    @Test
    void deberiaLanzarCampoObligatorioExceptionCuandoMensajeEsNull() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(null);

        // Act & Assert
        CampoObligatorioException exception = assertThrows(
                CampoObligatorioException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Error al guardar los datos en la base de datos", exception.getMessage());
    }

    @Test
    void deberiaLanzarCampoObligatorioExceptionCuandoErrorNoEsReconocido() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "ERROR: algún error desconocido de base de datos");

        // Act & Assert
        CampoObligatorioException exception = assertThrows(
                CampoObligatorioException.class,
                () -> DataIntegrityExceptionTranslator.throwSpecific(ex)
        );

        assertEquals("Error al guardar los datos en la base de datos", exception.getMessage());
    }
}
