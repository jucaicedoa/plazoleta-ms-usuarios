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
public class User {

    private Integer id;
    private String firstName;
    private String lastName;
    private String documentNumber;
    private String phone;
    private LocalDate birthDate;
    private String email;
    private String password;
    private Role role;
    private Integer restaurantId;

    public static User create(UserCreationData data, Role role) {
        return User.builder()
                .firstName(data.getFirstName())
                .lastName(data.getLastName())
                .documentNumber(data.getDocumentNumber())
                .phone(data.getPhone())
                .birthDate(data.getBirthDate())
                .email(data.getEmail())
                .password(data.getPassword())
                .role(role)
                .restaurantId(data.getRestaurantId())
                .build();
    }
}