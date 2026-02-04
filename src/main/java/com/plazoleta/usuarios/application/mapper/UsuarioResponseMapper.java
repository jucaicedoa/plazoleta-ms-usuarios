package com.plazoleta.usuarios.application.mapper;

import com.plazoleta.usuarios.application.dto.response.UsuarioResponseDto;
import com.plazoleta.usuarios.domain.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioResponseMapper {

    @Mapping(target = "rol", source = "rol.nombre")
    UsuarioResponseDto toResponse(Usuario usuario);
}