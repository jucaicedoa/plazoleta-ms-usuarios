package com.plazoleta.usuarios.application.handler;

import com.plazoleta.usuarios.application.dto.LoginDto;
import com.plazoleta.usuarios.domain.api.AuthServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHandler implements IAuthHandler {

    private final AuthServicePort authServicePort;

    @Override
    public String login(LoginDto dto) {
        return authServicePort.login(dto.getCorreo(), dto.getClave(), dto.getRestauranteId());
    }
}