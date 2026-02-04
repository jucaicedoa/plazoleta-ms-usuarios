package com.plazoleta.usuarios.domain.exception;

public class DocumentoYaRegistradoException extends RuntimeException {

    public DocumentoYaRegistradoException() {
        super("Ya existe un usuario con este número de documento");
    }

    public DocumentoYaRegistradoException(String mensaje) {
        super(mensaje);
    }
}