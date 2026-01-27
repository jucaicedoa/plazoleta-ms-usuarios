package com.plazoleta.usuarios.infraestructure.input.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDto {
    private Integer id;
    private String nombre;
    private String apellido;
    private String documento;
    private String celular;
    private LocalDate fechaNacimiento;
    private String correo;
    private String rol;
}