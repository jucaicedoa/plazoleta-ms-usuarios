package com.plazoleta.usuarios.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Objeto de parametros para agrupar los datos necesarios para crear un Usuario.
 * Resuelve el problema de tener muchos parámetros en el constructor Usuario( S107).
 *
 * Este objeto NO incluye el rol, ya que el rol se asigna según la operación
 * (crear propietario, crear empleado) y no viene del usuario.
 */
@Getter
@AllArgsConstructor
@Builder
public class DatosCreacionUsuario {

    private final String nombre;
    private final String apellido;
    private final String documento;
    private final String celular;
    private final LocalDate fechaNacimiento;
    private final String correo;
    private final String clave;
}
