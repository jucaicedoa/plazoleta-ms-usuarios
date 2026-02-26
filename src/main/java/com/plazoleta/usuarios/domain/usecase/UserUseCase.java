package com.plazoleta.usuarios.domain.usecase;

import com.plazoleta.usuarios.domain.api.UserCreationValidationPort;
import com.plazoleta.usuarios.domain.api.UserServicePort;
import com.plazoleta.usuarios.domain.model.Role;
import com.plazoleta.usuarios.domain.model.User;
import com.plazoleta.usuarios.domain.model.UserCreationData;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UserPersistencePort;

public class UserUseCase implements UserServicePort {

    private final UserPersistencePort persistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserCreationValidationPort userCreationValidationPort;

    public UserUseCase(UserPersistencePort persistencePort,
                       PasswordEncoderPort passwordEncoderPort,
                       UserCreationValidationPort userCreationValidationPort) {
        this.persistencePort = persistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.userCreationValidationPort = userCreationValidationPort;
    }

    @Override
    public void createOwner(UserCreationData data) {
        userCreationValidationPort.validate(data);
        UserCreationData dataWithEncodedPassword = data.withPassword(passwordEncoderPort.encode(data.getPassword()));
        Role ownerRole = new Role(null, "PROPIETARIO");
        User user = User.create(dataWithEncodedPassword, ownerRole);
        persistencePort.saveUser(user);
    }

    @Override
    public void createEmployee(UserCreationData data) {
        userCreationValidationPort.validate(data);
        UserCreationData dataWithEncodedPassword = data.withPassword(passwordEncoderPort.encode(data.getPassword()));
        Role employeeRole = new Role(null, "EMPLEADO");
        User user = User.create(dataWithEncodedPassword, employeeRole);
        persistencePort.saveUser(user);
    }

    @Override
    public User findById(Integer id) {
        return persistencePort.findById(id);
    }
}