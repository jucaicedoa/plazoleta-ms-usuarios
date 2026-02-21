package com.plazoleta.usuarios.domain.api;

import com.plazoleta.usuarios.domain.model.User;
import com.plazoleta.usuarios.domain.model.UserCreationData;

public interface UserServicePort {
    void createOwner(UserCreationData data);
    void createEmployee(UserCreationData data);
    User findById(Integer id);
}