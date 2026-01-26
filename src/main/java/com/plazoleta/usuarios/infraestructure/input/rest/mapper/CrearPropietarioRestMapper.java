package com.plazoleta.usuarios.infraestructure.input.rest.mapper;

import com.plazoleta.usuarios.application.dto.CrearPropietarioDto;
import com.plazoleta.usuarios.infraestructure.input.rest.dto.CrearPropietarioRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CrearPropietarioRestMapper {
    CrearPropietarioDto toApplicationDto(CrearPropietarioRequestDto requestDto);
}