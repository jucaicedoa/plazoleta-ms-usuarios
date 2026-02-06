package com.plazoleta.usuarios.infraestructure.out.jpa.mapper;

import com.plazoleta.usuarios.domain.model.Rol;
import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.infraestructure.out.jpa.entity.RoleEntity;
import com.plazoleta.usuarios.infraestructure.out.jpa.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioEntityMapper {

    @Mapping(target = "rol", source = "role")
    @Mapping(target = "restauranteId", source = "restaurantId")
    Usuario toDomain(UsuarioEntity entity);

    @Mapping(target = "role", source = "rol")
    @Mapping(target = "restaurantId", source = "restauranteId")
    UsuarioEntity toEntity(Usuario usuario);

    default Rol rolFromEntity(RoleEntity entity) {
        if (entity == null) return null;
        return new Rol(entity.getId(), entity.getName());
    }

    default RoleEntity rolToEntity(Rol rol) {
        if (rol == null) return null;
        RoleEntity entity = new RoleEntity();
        entity.setId(rol.getId());
        entity.setName(rol.getNombre());
        return entity;
    }
}