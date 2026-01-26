package com.plazoleta.usuarios.application.mapper;

import com.plazoleta.usuarios.application.dto.CrearPropietarioDto;
import com.plazoleta.usuarios.domain.model.DatosCreacionUsuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioRequestMapper {
    DatosCreacionUsuario toDatosCreacion(CrearPropietarioDto dto);
}