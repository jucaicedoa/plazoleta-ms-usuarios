package com.plazoleta.usuarios.application.handler;

import com.plazoleta.usuarios.application.dto.CrearEmpleadoDto;
import com.plazoleta.usuarios.application.dto.CrearPropietarioDto;
import com.plazoleta.usuarios.application.dto.response.UsuarioResponseDto;
import com.plazoleta.usuarios.application.mapper.UsuarioRequestMapper;
import com.plazoleta.usuarios.application.mapper.UsuarioResponseMapper;
import com.plazoleta.usuarios.domain.api.UsuarioServicePort;
import com.plazoleta.usuarios.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrearPropietarioHandler implements IUsuarioHandler {

    private final UsuarioServicePort usuarioServicePort;
    private final UsuarioRequestMapper mapper;
    private final UsuarioResponseMapper responseMapper;

    @Override
    public void crearPropietario(CrearPropietarioDto dto) {
        usuarioServicePort.crearPropietario(mapper.toDatosCreacion(dto));
    }

    @Override
    public void crearEmpleado(CrearEmpleadoDto dto, Integer restauranteId) {
        usuarioServicePort.crearEmpleado(mapper.toDatosCreacion(dto, restauranteId));
    }

    @Override
    public UsuarioResponseDto obtenerUsuarioPorId(Integer id) {
        Usuario usuario = usuarioServicePort.obtenerUsuarioPorId(id);
        return usuario != null ? responseMapper.toResponse(usuario) : null;
    }
}
