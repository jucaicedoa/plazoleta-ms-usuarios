package com.plazoleta.usuarios.application.handler;

import com.plazoleta.usuarios.application.dto.CreateEmployeeDto;
import com.plazoleta.usuarios.application.dto.CreateOwnerDto;
import com.plazoleta.usuarios.application.dto.response.UserResponseDto;

public interface IUserHandler {

    void createOwner(CreateOwnerDto dto);
    void createEmployee(CreateEmployeeDto dto, Integer restaurantId);
    UserResponseDto findById(Integer id);
}