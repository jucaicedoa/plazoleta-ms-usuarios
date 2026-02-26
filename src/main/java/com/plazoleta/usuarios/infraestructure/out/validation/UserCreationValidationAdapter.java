package com.plazoleta.usuarios.infraestructure.out.validation;

import com.plazoleta.usuarios.domain.api.UserCreationValidationPort;
import com.plazoleta.usuarios.domain.exception.InvalidEmailException;
import com.plazoleta.usuarios.domain.exception.InvalidFieldException;
import com.plazoleta.usuarios.domain.exception.UserUnderAgeException;
import com.plazoleta.usuarios.domain.model.UserCreationData;
import com.plazoleta.usuarios.domain.spi.UserPersistencePort;
import java.time.LocalDate;
import java.time.Period;

public class UserCreationValidationAdapter implements UserCreationValidationPort {

    private final UserPersistencePort userPersistencePort;

    public UserCreationValidationAdapter(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public void validate(UserCreationData data) {
        if (!data.getDocumentNumber().matches("\\d+")) {
            throw new InvalidFieldException("Invalid document");
        }

        if (!data.getEmail().matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidEmailException();
        }

        if (!data.getPhone().matches("^\\+?\\d{1,13}$")) {
            throw new InvalidFieldException("Invalid phone");
        }

        if (data.getBirthDate() == null
                || Period.between(data.getBirthDate(), LocalDate.now()).getYears() < 18) {
            throw new UserUnderAgeException();
        }

        if (userPersistencePort.existsByEmail(data.getEmail())) {
            throw new InvalidFieldException("Email already registered");
        }
    }
}