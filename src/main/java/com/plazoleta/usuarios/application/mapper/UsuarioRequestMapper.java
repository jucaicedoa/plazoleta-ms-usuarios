package com.plazoleta.usuarios.application.mapper;

import com.plazoleta.usuarios.application.dto.CrearEmpleadoDto;
import com.plazoleta.usuarios.application.dto.CrearPropietarioDto;
import com.plazoleta.usuarios.domain.model.DatosCreacionUsuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioRequestMapper {

    @Mapping(target = "restauranteId", expression = "java((Integer) null)")
    DatosCreacionUsuario toDatosCreacion(CrearPropietarioDto dto);

    @Mapping(target = "restauranteId", source = "restauranteId")
    DatosCreacionUsuario toDatosCreacion(CrearEmpleadoDto dto, Integer restauranteId);
}
