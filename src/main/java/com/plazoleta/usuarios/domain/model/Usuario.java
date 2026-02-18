package com.plazoleta.usuarios.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

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
    /** ID del restaurante al que pertenece (empleados y propietarios). */
    private Integer restauranteId;

    public static Usuario crear(DatosCreacionUsuario datos, Rol rol) {
        return Usuario.builder()
                .nombre(datos.getNombre())
                .apellido(datos.getApellido())
                .documento(datos.getDocumento())
                .celular(datos.getCelular())
                .fechaNacimiento(datos.getFechaNacimiento())
                .correo(datos.getCorreo())
                .clave(datos.getClave())
                .rol(rol)
                .restauranteId(datos.getRestauranteId())
                .build();
    }
}
