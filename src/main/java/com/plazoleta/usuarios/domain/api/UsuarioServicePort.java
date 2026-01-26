package com.plazoleta.usuarios.domain.api;

import com.plazoleta.usuarios.domain.model.DatosCreacionUsuario;

public interface UsuarioServicePort {
    void crearPropietario(DatosCreacionUsuario datos);
}