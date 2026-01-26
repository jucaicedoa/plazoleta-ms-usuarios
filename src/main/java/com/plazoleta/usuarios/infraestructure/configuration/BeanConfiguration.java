package com.plazoleta.usuarios.infraestructure.configuration;

import com.plazoleta.usuarios.domain.api.UsuarioServicePort;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UsuarioPersistencePort;
import com.plazoleta.usuarios.domain.usecase.CrearPropietarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final UsuarioPersistencePort usuarioPersistencePort;

    @Bean
    public PasswordEncoderPort passwordEncoderPort() {
        return password -> new BCryptPasswordEncoder().encode(password);
    }

    @Bean
    public UsuarioServicePort usuarioServicePort() {
        return new CrearPropietarioUseCase(usuarioPersistencePort, passwordEncoderPort());
    }
}