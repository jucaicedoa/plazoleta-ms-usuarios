package com.plazoleta.usuarios.domain.api;

public interface AuthServicePort {

    String login(String email, String password, Integer restaurantId);
}