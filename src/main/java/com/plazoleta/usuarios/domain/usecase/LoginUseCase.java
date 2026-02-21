package com.plazoleta.usuarios.domain.usecase;

import com.plazoleta.usuarios.domain.api.AuthServicePort;
import com.plazoleta.usuarios.domain.exception.InvalidCredentialsException;
import com.plazoleta.usuarios.domain.model.User;
import com.plazoleta.usuarios.domain.spi.JwtProviderPort;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UserPersistencePort;
import java.util.Optional;

public class LoginUseCase implements AuthServicePort {

    private static final String INVALID_CREDENTIALS = "Invalid credentials";

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtProviderPort jwtProviderPort;

    public LoginUseCase(UserPersistencePort userPersistencePort,
                        PasswordEncoderPort passwordEncoderPort,
                        JwtProviderPort jwtProviderPort) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtProviderPort = jwtProviderPort;
    }

    @Override
    public String login(String email, String password, Integer restaurantId) {
        Optional<User> userOpt = userPersistencePort.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }
        User user = userOpt.get();
        if (!passwordEncoderPort.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }
        String role = user.getRole() != null ? user.getRole().getName() : "";
        return jwtProviderPort.generateToken(user.getId(), user.getEmail(), role, restaurantId);
    }
}