package com.plazoleta.usuarios.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "La clave es obligatoria")
    private String clave;

    private Integer restauranteId;
}
