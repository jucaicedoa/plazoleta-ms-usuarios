package com.plazoleta.usuarios.domain.spi;
import com.plazoleta.usuarios.domain.model.Usuario;

public interface UsuarioPersistencePort {

    Usuario guardarUsuario(Usuario usuario);
    boolean existeCorreo(String correo);
    Usuario obtenerUsuarioPorId(Integer id);
}