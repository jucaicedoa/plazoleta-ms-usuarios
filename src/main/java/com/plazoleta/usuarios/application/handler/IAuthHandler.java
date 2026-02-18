package com.plazoleta.usuarios.application.handler;

import com.plazoleta.usuarios.application.dto.LoginDto;

public interface IAuthHandler {
    String login(LoginDto dto);
}