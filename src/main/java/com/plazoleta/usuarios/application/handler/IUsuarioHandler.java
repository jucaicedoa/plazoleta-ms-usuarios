package com.plazoleta.usuarios.application.handler;

import com.plazoleta.usuarios.application.dto.CrearPropietarioDto;
import com.plazoleta.usuarios.application.dto.response.UsuarioResponseDto;

public interface IUsuarioHandler {

    void crearPropietario(CrearPropietarioDto dto);

    UsuarioResponseDto obtenerUsuarioPorId(Integer id);
}