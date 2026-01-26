package com.plazoleta.usuarios.infraestructure.out.jpa.repository;

import com.plazoleta.usuarios.infraestructure.out.jpa.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(String name);
}