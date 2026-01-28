package com.plazoleta.usuarios.infraestructure.input.rest.mapper;

import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.infraestructure.input.rest.dto.UsuarioResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioResponseMapper {

    @Mapping(target = "rol", source = "rol.nombre")
    UsuarioResponseDto toResponse(Usuario usuario);
}