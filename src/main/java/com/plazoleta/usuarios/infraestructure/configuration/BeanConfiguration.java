package com.plazoleta.usuarios.infraestructure.configuration;

import com.plazoleta.usuarios.domain.api.UsuarioServicePort;
import com.plazoleta.usuarios.domain.model.DatosCreacionUsuario;
import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UsuarioPersistencePort;
import com.plazoleta.usuarios.domain.usecase.CrearPropietarioUseCase;
import com.plazoleta.usuarios.domain.usecase.ObtenerUsuarioPorIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    @Primary
    public UsuarioServicePort usuarioServicePort() {
        CrearPropietarioUseCase crearPropietarioUseCase =
                new CrearPropietarioUseCase(usuarioPersistencePort, passwordEncoderPort());
        ObtenerUsuarioPorIdUseCase obtenerUsuarioUseCase =
                new ObtenerUsuarioPorIdUseCase(usuarioPersistencePort);

        return new UsuarioServicePort() {
            @Override
            public void crearPropietario(DatosCreacionUsuario datos) {
                crearPropietarioUseCase.crearPropietario(datos);
            }

            @Override
            public Usuario obtenerUsuarioPorId(Integer id) {
                return obtenerUsuarioUseCase.obtenerUsuarioPorId(id);
            }
        };
    }
}