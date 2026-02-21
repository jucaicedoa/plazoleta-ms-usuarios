package com.plazoleta.usuarios.infraestructure.configuration;

import com.plazoleta.usuarios.domain.api.AuthServicePort;
import com.plazoleta.usuarios.domain.api.UserServicePort;
import com.plazoleta.usuarios.domain.spi.JwtProviderPort;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UserPersistencePort;
import com.plazoleta.usuarios.domain.usecase.LoginUseCase;
import com.plazoleta.usuarios.domain.usecase.UserUseCase;
import com.plazoleta.usuarios.infraestructure.out.jpa.adapter.UserJpaAdapter;
import com.plazoleta.usuarios.infraestructure.out.jpa.mapper.UserEntityMapper;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.RoleRepository;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserEntityMapper userEntityMapper;
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
    public UserPersistencePort userPersistencePort() {
        return new UserJpaAdapter(userRepository, roleRepository, userEntityMapper);
    }

    @Bean
    public UserServicePort userServicePort() {
        return new UserUseCase(userPersistencePort(), passwordEncoderPort());
    }

    @Bean
    public AuthServicePort authServicePort() {
        LoginUseCase loginUseCase = new LoginUseCase(
                userPersistencePort(),
                passwordEncoderPort(),
                jwtProviderPort
        );
        return loginUseCase::login;
    }
}