package com.plazoleta.usuarios.infraestructure.configuration;

import com.plazoleta.usuarios.domain.api.UsuarioServicePort;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UsuarioPersistencePort;
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

    @Bean
    public PasswordEncoderPort passwordEncoderPort() {
        return password -> new BCryptPasswordEncoder().encode(password);
    }

    @Bean
    public UsuarioPersistencePort usuarioPersistencePort() {
        return new UsuarioJpaAdapter(usuarioRepository, roleRepository, usuarioEntityMapper);
    }

    @Bean
    public UsuarioServicePort usuarioServicePort() {
        return new UsuarioUseCase(usuarioPersistencePort(), passwordEncoderPort());
    }
}