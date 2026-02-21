package com.plazoleta.usuarios.infraestructure.out.jpa.mapper;

import com.plazoleta.usuarios.domain.model.Role;
import com.plazoleta.usuarios.domain.model.User;
import com.plazoleta.usuarios.infraestructure.out.jpa.entity.RoleEntity;
import com.plazoleta.usuarios.infraestructure.out.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    @Mapping(target = "role", source = "role")
    @Mapping(target = "restaurantId", source = "restaurantId")
    User toDomain(UserEntity entity);

    @Mapping(target = "role", source = "role")
    @Mapping(target = "restaurantId", source = "restaurantId")
    UserEntity toEntity(User user);

    default Role roleFromEntity(RoleEntity entity) {
        if (entity == null) return null;
        return new Role(entity.getId(), entity.getName());
    }

    default RoleEntity roleToEntity(Role role) {
        if (role == null) return null;
        RoleEntity entity = new RoleEntity();
        entity.setId(role.getId());
        entity.setName(role.getName());
        return entity;
    }
}