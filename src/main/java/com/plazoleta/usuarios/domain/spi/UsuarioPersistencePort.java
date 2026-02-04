package com.plazoleta.usuarios.domain.spi;

import com.plazoleta.usuarios.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioPersistencePort {

    Usuario guardarUsuario(Usuario usuario);
    boolean existeCorreo(String correo);
    Usuario obtenerUsuarioPorId(Integer id);
    Optional<Usuario> buscarPorCorreo(String correo);
}