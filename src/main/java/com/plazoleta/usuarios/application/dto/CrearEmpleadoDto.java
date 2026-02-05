package com.plazoleta.usuarios.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearEmpleadoDto {
    private String nombre;
    private String apellido;
    private String documento;
    private String celular;
    private LocalDate fechaNacimiento;
    private String correo;
    private String clave;
}