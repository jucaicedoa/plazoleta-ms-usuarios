package com.plazoleta.usuarios.infraestructure.out.jpa.exception;

import com.plazoleta.usuarios.domain.exception.CampoObligatorioException;
import com.plazoleta.usuarios.domain.exception.CorreoYaRegistradoException;
import com.plazoleta.usuarios.domain.exception.DocumentoYaRegistradoException;
import com.plazoleta.usuarios.domain.exception.ValorExcedeLongitudException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Excepciones de integridad de base de datos (Spring) a excepciones del dominio.
 */
public final class DataIntegrityExceptionTranslator {

    private DataIntegrityExceptionTranslator() {
    }

    public static void throwSpecific(DataIntegrityViolationException ex) {
        String mensaje = ex.getMessage();
        if (mensaje == null) {
            throw new CampoObligatorioException("Error al guardar los datos en la base de datos");
        }

        if (isUniqueConstraintViolation(mensaje)) {
            throwUniqueConstraint(mensaje);
        } else if (isValueTooLongError(mensaje)) {
            throwValueTooLong(mensaje);
        } else if (isNotNullViolation(mensaje)) {
            throw new CampoObligatorioException();
        }

        throw new CampoObligatorioException("Error al guardar los datos en la base de datos");
    }

    private static boolean isUniqueConstraintViolation(String mensaje) {
        return mensaje.contains("unique constraint") || mensaje.contains("duplicate key");
    }

    private static boolean isValueTooLongError(String mensaje) {
        return mensaje.contains("demasiado largo") || mensaje.contains("value too long");
    }

    private static boolean isNotNullViolation(String mensaje) {
        return mensaje.contains("not-null") || mensaje.contains("null value");
    }

    private static void throwUniqueConstraint(String mensaje) {
        if (mensaje.contains("email")) {
            throw new CorreoYaRegistradoException();
        }
        if (mensaje.contains("document")) {
            throw new DocumentoYaRegistradoException();
        }
        throw new CorreoYaRegistradoException("Ya existe un registro con estos datos");
    }

    private static void throwValueTooLong(String mensaje) {
        if (mensaje.contains("phone") || mensaje.contains("varying(13)")) {
            throw new ValorExcedeLongitudException(
                    "El número de celular no puede tener más de 13 caracteres", "celular");
        }
        if (mensaje.contains("document_number")) {
            throw new ValorExcedeLongitudException(
                    "El número de documento excede la longitud máxima permitida", "documento");
        }
        if (mensaje.contains("email")) {
            throw new ValorExcedeLongitudException(
                    "El correo electrónico excede la longitud máxima permitida", "correo");
        }
        if (mensaje.contains("first_name")) {
            throw new ValorExcedeLongitudException(
                    "El nombre excede la longitud máxima permitida", "nombre");
        }
        if (mensaje.contains("last_name")) {
            throw new ValorExcedeLongitudException(
                    "El apellido excede la longitud máxima permitida", "apellido");
        }
        if (mensaje.contains("password")) {
            throw new ValorExcedeLongitudException(
                    "La contraseña excede la longitud máxima permitida", "clave");
        }
        throw new ValorExcedeLongitudException("El valor excede la longitud máxima permitida", "desconocido");
    }
}