package com.plazoleta.usuarios.application.mapper;

import com.plazoleta.usuarios.application.dto.response.UserResponseDto;
import com.plazoleta.usuarios.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserResponseMapper {

    @Mapping(target = "role", source = "role.name")
    UserResponseDto toResponse(User user);
}