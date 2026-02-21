package com.plazoleta.usuarios.application.mapper;

import com.plazoleta.usuarios.application.dto.CreateEmployeeDto;
import com.plazoleta.usuarios.application.dto.CreateOwnerDto;
import com.plazoleta.usuarios.domain.model.UserCreationData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRequestMapper {

    @Mapping(target = "restaurantId", expression = "java((Integer) null)")
    UserCreationData toUserCreationData(CreateOwnerDto dto);

    @Mapping(target = "restaurantId", source = "restaurantId")
    UserCreationData toUserCreationData(CreateEmployeeDto dto, Integer restaurantId);
}