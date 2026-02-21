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
    private String email;
    private String role;
    private Integer restaurantId;
}