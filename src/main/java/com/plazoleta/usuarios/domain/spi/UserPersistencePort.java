package com.plazoleta.usuarios.domain.spi;

import com.plazoleta.usuarios.domain.model.User;
import java.util.Optional;

public interface UserPersistencePort {

    User saveUser(User user);
    boolean existsByEmail(String email);
    User findById(Integer id);
    Optional<User> findByEmail(String email);
}
