package com.plazoleta.usuarios.infraestructure.out.jpa.adapter;

import com.plazoleta.usuarios.domain.exception.RoleNotFoundException;
import com.plazoleta.usuarios.domain.model.User;
import com.plazoleta.usuarios.domain.spi.UserPersistencePort;
import com.plazoleta.usuarios.infraestructure.out.jpa.entity.UserEntity;
import com.plazoleta.usuarios.infraestructure.out.jpa.exception.DataIntegrityExceptionTranslator;
import com.plazoleta.usuarios.infraestructure.out.jpa.mapper.UserEntityMapper;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.RoleRepository;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Optional;

@RequiredArgsConstructor
public class UserJpaAdapter implements UserPersistencePort {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserEntityMapper mapper;

    @Override
    public User saveUser(User user) {
        UserEntity entity = mapper.toEntity(user);
        String roleName = user.getRole() != null && user.getRole().getName() != null
                ? user.getRole().getName()
                : "PROPIETARIO";
        entity.setRole(roleRepository.findByName(roleName).orElseThrow(
                () -> new RoleNotFoundException("Role " + roleName + " not found in database")
        ));
        try {
            UserEntity savedEntity = userRepository.save(entity);
            return mapper.toDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            DataIntegrityExceptionTranslator.throwSpecific(e);
            return null;
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findById(Integer id) {
        UserEntity entity = userRepository.findById(id.longValue()).orElse(null);
        return entity != null ? mapper.toDomain(entity) : null;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(mapper::toDomain);
    }
}