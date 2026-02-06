package com.plazoleta.usuarios.domain.api;

public interface AuthServicePort {

    /**
     * Inicia sesión y genera el token. Si es propietario y envía restauranteId,
     * se incluye en el token para usarlo al crear empleados (sin pasarlo en el body).
     */
    String login(String correo, String clave, Integer restauranteId);
}