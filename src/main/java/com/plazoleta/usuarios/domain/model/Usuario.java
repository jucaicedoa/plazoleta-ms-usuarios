package com.plazoleta.usuarios.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Usuario {

    private Integer id;
    private String nombre;
    private String apellido;
    private String documento;
    private String celular;
    private LocalDate fechaNacimiento;
    private String correo;
    private String clave;
    private Rol rol;

    /**
     * Metodo factory para crear un Usuario con validación de mayor de edad.
     *
     * @param datos Objeto con los datos del usuario
     * @param rol Rol que se asignará al usuario
     * @return Usuario creado y validado
     * @throws IllegalArgumentException si el usuario es menor de edad
     */
    public static Usuario crear(DatosCreacionUsuario datos, Rol rol) {
        validarMayorEdad(datos.getFechaNacimiento());

        return Usuario.builder()
                .nombre(datos.getNombre())
                .apellido(datos.getApellido())
                .documento(datos.getDocumento())
                .celular(datos.getCelular())
                .fechaNacimiento(datos.getFechaNacimiento())
                .correo(datos.getCorreo())
                .clave(datos.getClave())
                .rol(rol)
                .build();
    }

    private static void validarMayorEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null || Period.between(fechaNacimiento, LocalDate.now()).getYears() < 18) {
            throw new IllegalArgumentException("El usuario debe ser mayor de edad");
        }
    }
}
