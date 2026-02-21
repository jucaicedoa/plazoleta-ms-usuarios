package com.plazoleta.usuarios.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserCreationDataTest {

    @Test
    void shouldCreateUserCreationDataWithBuilder() {
        LocalDate birthDate = LocalDate.now().minusYears(25);

        UserCreationData data = UserCreationData.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .documentNumber("12345678")
                .phone("+573001234567")
                .birthDate(birthDate)
                .email("juan@example.com")
                .password("password123")
                .restaurantId(null)
                .build();

        assertNotNull(data);
        assertEquals("Juan", data.getFirstName());
        assertEquals("Pérez", data.getLastName());
        assertEquals("12345678", data.getDocumentNumber());
        assertEquals("+573001234567", data.getPhone());
        assertEquals(birthDate, data.getBirthDate());
        assertEquals("juan@example.com", data.getEmail());
        assertEquals("password123", data.getPassword());
    }

    @Test
    void shouldCreateUserCreationDataWithConstructor() {
        LocalDate birthDate = LocalDate.now().minusYears(30);

        UserCreationData data = new UserCreationData(
                "María",
                "García",
                "87654321",
                "+573009876543",
                birthDate,
                "maria@example.com",
                "securepass",
                null
        );

        assertNotNull(data);
        assertEquals("María", data.getFirstName());
        assertEquals("García", data.getLastName());
        assertEquals("87654321", data.getDocumentNumber());
        assertEquals("+573009876543", data.getPhone());
        assertEquals(birthDate, data.getBirthDate());
        assertEquals("maria@example.com", data.getEmail());
        assertEquals("securepass", data.getPassword());
    }
}