package com.plazoleta.usuarios.domain.usecase;

import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.domain.spi.UsuarioPersistencePort;

public class ObtenerUsuarioPorIdUseCase {

    private final UsuarioPersistencePort persistencePort;

    public ObtenerUsuarioPorIdUseCase(UsuarioPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    public Usuario obtenerUsuarioPorId(Integer id) {
        return persistencePort.obtenerUsuarioPorId(id);
    }
}