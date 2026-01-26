package com.plazoleta.usuarios.infraestructure.out.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario", schema = "usuarios")
@Getter
@Setter
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String nombre;

    @Column(name = "last_name")
    private String apellido;

    @Column(name = "document_number")
    private String documento;

    @Column(name = "phone")
    private String celular;

    @Column(name = "birth_date")
    private LocalDate fechaNacimiento;

    @Column(name = "email")
    private String correo;

    @Column(name = "password")
    private String clave;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @Column(name = "restaurant_id")
    private Integer restaurantId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}