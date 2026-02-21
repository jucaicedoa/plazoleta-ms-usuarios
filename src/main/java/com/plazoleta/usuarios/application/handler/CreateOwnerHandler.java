package com.plazoleta.usuarios.application.handler;

import com.plazoleta.usuarios.application.dto.CreateEmployeeDto;
import com.plazoleta.usuarios.application.dto.CreateOwnerDto;
import com.plazoleta.usuarios.application.dto.response.UserResponseDto;
import com.plazoleta.usuarios.application.mapper.UserRequestMapper;
import com.plazoleta.usuarios.application.mapper.UserResponseMapper;
import com.plazoleta.usuarios.domain.api.UserServicePort;
import com.plazoleta.usuarios.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateOwnerHandler implements IUserHandler {

    private final UserServicePort userServicePort;
    private final UserRequestMapper mapper;
    private final UserResponseMapper responseMapper;

    @Override
    public void createOwner(CreateOwnerDto dto) {
        userServicePort.createOwner(mapper.toUserCreationData(dto));
    }

    @Override
    public void createEmployee(CreateEmployeeDto dto, Integer restaurantId) {
        userServicePort.createEmployee(mapper.toUserCreationData(dto, restaurantId));
    }

    @Override
    public UserResponseDto findById(Integer id) {
        User user = userServicePort.findById(id);
        return user != null ? responseMapper.toResponse(user) : null;
    }
}