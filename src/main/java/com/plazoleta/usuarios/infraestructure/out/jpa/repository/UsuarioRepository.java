package com.plazoleta.usuarios.infraestructure.out.jpa.repository;

import com.plazoleta.usuarios.infraestructure.out.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    boolean existsByCorreo(String correo);
    Optional<UsuarioEntity> findByCorreo(String correo);
}