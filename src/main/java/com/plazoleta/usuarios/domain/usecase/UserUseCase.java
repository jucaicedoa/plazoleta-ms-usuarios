package com.plazoleta.usuarios.domain.usecase;

import com.plazoleta.usuarios.domain.api.UserServicePort;
import com.plazoleta.usuarios.domain.exception.EmailAlreadyRegisteredException;
import com.plazoleta.usuarios.domain.exception.InvalidFieldException;
import com.plazoleta.usuarios.domain.exception.InvalidEmailException;
import com.plazoleta.usuarios.domain.exception.UserUnderAgeException;
import com.plazoleta.usuarios.domain.model.Role;
import com.plazoleta.usuarios.domain.model.User;
import com.plazoleta.usuarios.domain.model.UserCreationData;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UserPersistencePort;
import java.time.LocalDate;
import java.time.Period;

public class UserUseCase implements UserServicePort {

    private final UserPersistencePort persistencePort;
    private final PasswordEncoderPort passwordEncoderPort;

    public UserUseCase(UserPersistencePort persistencePort, PasswordEncoderPort passwordEncoderPort) {
        this.persistencePort = persistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public void createOwner(UserCreationData data) {
        validate(data);
        UserCreationData dataWithEncodedPassword = data.withPassword(passwordEncoderPort.encode(data.getPassword()));
        Role ownerRole = new Role(null, "PROPIETARIO");
        User user = User.create(dataWithEncodedPassword, ownerRole);
        persistencePort.saveUser(user);
    }

    @Override
    public void createEmployee(UserCreationData data) {
        validate(data);
        UserCreationData dataWithEncodedPassword = data.withPassword(passwordEncoderPort.encode(data.getPassword()));
        Role employeeRole = new Role(null, "EMPLEADO");
        User user = User.create(dataWithEncodedPassword, employeeRole);
        persistencePort.saveUser(user);
    }

    @Override
    public User findById(Integer id) {
        return persistencePort.findById(id);
    }

    private void validate(UserCreationData data) {
        if (!data.getDocumentNumber().matches("\\d+"))
            throw new InvalidFieldException("Invalid document");

        if (!data.getEmail().matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$"))
            throw new InvalidEmailException();

        if (!data.getPhone().matches("^\\+?\\d{1,13}$"))
            throw new InvalidFieldException("Invalid phone");

        if (data.getBirthDate() == null
                || Period.between(data.getBirthDate(), LocalDate.now()).getYears() < 18)
            throw new UserUnderAgeException();

        if (persistencePort.existsByEmail(data.getEmail()))
            throw new EmailAlreadyRegisteredException("Email already registered");
    }
}