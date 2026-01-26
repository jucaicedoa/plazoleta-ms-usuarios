package com.plazoleta.usuarios.infraestructure.out.jpa.adapter;

import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.domain.spi.UsuarioPersistencePort;
import com.plazoleta.usuarios.infraestructure.out.jpa.entity.UsuarioEntity;
import com.plazoleta.usuarios.infraestructure.out.jpa.mapper.UsuarioEntityMapper;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.RoleRepository;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioJpaAdapter implements UsuarioPersistencePort {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final UsuarioEntityMapper mapper;

    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        UsuarioEntity entity = mapper.toEntity(usuario);
        entity.setRole(roleRepository.findByName("PROPIETARIO").orElseThrow(
                () -> new RuntimeException("Rol PROPIETARIO no encontrado en la base de datos")
        ));
        UsuarioEntity savedEntity = usuarioRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }
}