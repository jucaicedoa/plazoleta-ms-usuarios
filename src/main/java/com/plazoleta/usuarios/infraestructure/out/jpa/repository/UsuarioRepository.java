package com.plazoleta.usuarios.infraestructure.out.jpa.repository;

import com.plazoleta.usuarios.infraestructure.out.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    boolean existsByCorreo(String correo);
}