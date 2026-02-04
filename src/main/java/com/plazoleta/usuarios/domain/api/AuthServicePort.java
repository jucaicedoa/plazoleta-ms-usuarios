package com.plazoleta.usuarios.domain.api;

public interface AuthServicePort {

    String login(String correo, String clave);
}