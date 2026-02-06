package com.plazoleta.usuarios.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenClaims {
    private Integer id;
    private String correo;
    private String rol;
    /** ID del restaurante del propietario; solo presente cuando el usuario es PROPIETARIO. */
    private Integer restauranteId;
}