package com.plazoleta.usuarios.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.LocalDate;


@Getter
@AllArgsConstructor
@Builder
public class UserCreationData {

    private final String firstName;
    private final String lastName;
    private final String documentNumber;
    private final String phone;
    private final LocalDate birthDate;
    private final String email;
    @With
    private final String password;
    private final Integer restaurantId;
}