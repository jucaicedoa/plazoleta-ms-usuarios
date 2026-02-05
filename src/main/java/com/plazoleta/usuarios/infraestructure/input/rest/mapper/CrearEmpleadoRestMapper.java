package com.plazoleta.usuarios.infraestructure.input.rest.mapper;

import com.plazoleta.usuarios.application.dto.CrearEmpleadoDto;
import com.plazoleta.usuarios.infraestructure.input.rest.dto.CrearEmpleadoRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CrearEmpleadoRestMapper {
    CrearEmpleadoDto toApplicationDto(CrearEmpleadoRequestDto requestDto);
}