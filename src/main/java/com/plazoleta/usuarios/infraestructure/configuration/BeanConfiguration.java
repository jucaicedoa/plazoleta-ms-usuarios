package com.plazoleta.usuarios.infraestructure.configuration;

import com.plazoleta.usuarios.domain.api.AuthServicePort;
import com.plazoleta.usuarios.domain.api.UsuarioServicePort;
import com.plazoleta.usuarios.domain.spi.JwtProviderPort;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UsuarioPersistencePort;
import com.plazoleta.usuarios.domain.usecase.LoginUseCase;
import com.plazoleta.usuarios.domain.usecase.UsuarioUseCase;
import com.plazoleta.usuarios.infraestructure.out.jpa.adapter.UsuarioJpaAdapter;
import com.plazoleta.usuarios.infraestructure.out.jpa.mapper.UsuarioEntityMapper;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.RoleRepository;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final UsuarioEntityMapper usuarioEntityMapper;
    private final JwtProviderPort jwtProviderPort;

    @Bean
    public PasswordEncoderPort passwordEncoderPort() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return new PasswordEncoderPort() {
            @Override
            public String encode(String password) {
                return encoder.encode(password);
            }
            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return encoder.matches(rawPassword, encodedPassword);
            }
        };
    }

    @Bean
    public UsuarioPersistencePort usuarioPersistencePort() {
        return new UsuarioJpaAdapter(usuarioRepository, roleRepository, usuarioEntityMapper);
    }

    @Bean
    public UsuarioServicePort usuarioServicePort() {
        return new UsuarioUseCase(usuarioPersistencePort(), passwordEncoderPort());
    }

    @Bean
    public AuthServicePort authServicePort() {
        LoginUseCase loginUseCase = new LoginUseCase(
                usuarioPersistencePort(),
                passwordEncoderPort(),
                jwtProviderPort
        );
        return loginUseCase::login;
    }
}