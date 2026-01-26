package com.plazoleta.usuarios.infraestructure.out.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rol", schema = "usuarios")
@Getter
@Setter
public class RoleEntity {

    @Id
    private Integer id;

    private String name;
}