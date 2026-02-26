package com.plazoleta.usuarios.domain.api;

import com.plazoleta.usuarios.domain.model.UserCreationData;

public interface UserCreationValidationPort {

    void validate(UserCreationData data);
}