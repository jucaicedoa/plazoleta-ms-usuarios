package com.plazoleta.usuarios.domain.usecase;

import com.plazoleta.usuarios.domain.api.AuthServicePort;
import com.plazoleta.usuarios.domain.exception.CredencialesInvalidasException;
import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.domain.spi.JwtProviderPort;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UsuarioPersistencePort;

import java.util.Optional;

public class LoginUseCase implements AuthServicePort {

    private static final String CREDENCIALES_INVALIDAS = "Credenciales inválidas";

    private final UsuarioPersistencePort usuarioPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtProviderPort jwtProviderPort;

    public LoginUseCase(UsuarioPersistencePort usuarioPersistencePort,
                        PasswordEncoderPort passwordEncoderPort,
                        JwtProviderPort jwtProviderPort) {
        this.usuarioPersistencePort = usuarioPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtProviderPort = jwtProviderPort;
    }

    @Override
    public String login(String correo, String clave, Integer restauranteId) {
        Optional<Usuario> usuarioOpt = usuarioPersistencePort.buscarPorCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            throw new CredencialesInvalidasException(CREDENCIALES_INVALIDAS);
        }
        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoderPort.matches(clave, usuario.getClave())) {
            throw new CredencialesInvalidasException(CREDENCIALES_INVALIDAS);
        }
        String rol = usuario.getRol() != null ? usuario.getRol().getNombre() : "";
        return jwtProviderPort.generarToken(usuario.getId(), usuario.getCorreo(), rol, restauranteId);
    }
}