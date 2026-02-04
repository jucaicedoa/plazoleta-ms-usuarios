package com.plazoleta.usuarios.infraestructure.out.jpa.adapter;

import com.plazoleta.usuarios.domain.exception.RolNoEncontradoException;
import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.domain.spi.UsuarioPersistencePort;
import com.plazoleta.usuarios.infraestructure.out.jpa.entity.UsuarioEntity;
import com.plazoleta.usuarios.infraestructure.out.jpa.exception.DataIntegrityExceptionTranslator;
import com.plazoleta.usuarios.infraestructure.out.jpa.mapper.UsuarioEntityMapper;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.RoleRepository;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

@RequiredArgsConstructor
public class UsuarioJpaAdapter implements UsuarioPersistencePort {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final UsuarioEntityMapper mapper;

    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        UsuarioEntity entity = mapper.toEntity(usuario);
        entity.setRole(roleRepository.findByName("PROPIETARIO").orElseThrow(
                () -> new RolNoEncontradoException("Rol PROPIETARIO no encontrado en la base de datos")
        ));
        try {
            UsuarioEntity savedEntity = usuarioRepository.save(entity);
            return mapper.toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            DataIntegrityExceptionTranslator.throwSpecific(e);
            return null;
        }
    }

    @Override
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Override
    public Usuario obtenerUsuarioPorId(Integer id) {
        UsuarioEntity entity = usuarioRepository.findById(id.longValue()).orElse(null);
        return entity != null ? mapper.toDomain(entity) : null;
    }
}