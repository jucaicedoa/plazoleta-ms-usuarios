package com.plazoleta.usuarios.domain.api;

import com.plazoleta.usuarios.domain.model.DatosCreacionUsuario;
import com.plazoleta.usuarios.domain.model.Usuario;

public interface UsuarioServicePort {
    void crearPropietario(DatosCreacionUsuario datos);
    Usuario obtenerUsuarioPorId(Integer id);
}