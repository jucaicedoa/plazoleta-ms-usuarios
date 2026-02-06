package com.plazoleta.usuarios.infraestructure.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "La clave es obligatoria")
    private String clave;

    /** Restaurante con el que trabaja en la sesión (propietario). Opcional; si se envía, se incluye en el token para crear empleados. */
    private Integer restauranteId;
}