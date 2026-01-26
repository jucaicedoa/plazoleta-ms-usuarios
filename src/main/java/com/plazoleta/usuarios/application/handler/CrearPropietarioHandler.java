package com.plazoleta.usuarios.application.handler;

import com.plazoleta.usuarios.application.dto.CrearPropietarioDto;
import com.plazoleta.usuarios.application.mapper.UsuarioRequestMapper;
import com.plazoleta.usuarios.domain.api.UsuarioServicePort;
import com.plazoleta.usuarios.domain.model.DatosCreacionUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrearPropietarioHandler {

    private final UsuarioServicePort usuarioServicePort;
    private final UsuarioRequestMapper mapper;

    public void crearPropietario(CrearPropietarioDto dto) {
        DatosCreacionUsuario datos = mapper.toDatosCreacion(dto);
        usuarioServicePort.crearPropietario(datos);
    }
}