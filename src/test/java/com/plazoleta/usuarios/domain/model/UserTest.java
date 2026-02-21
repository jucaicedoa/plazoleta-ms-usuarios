package com.plazoleta.usuarios.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void shouldCreateUserWithValidData() {
        LocalDate birthDate = LocalDate.now().minusYears(20);
        Role role = new Role(2, "PROPIETARIO");

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

        User user = User.create(data, role);

        assertNotNull(user);
        assertEquals("Juan", user.getFirstName());
        assertEquals("Pérez", user.getLastName());
        assertEquals("12345678", user.getDocumentNumber());
        assertEquals("+573001234567", user.getPhone());
        assertEquals(birthDate, user.getBirthDate());
        assertEquals("juan@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(role, user.getRole());
    }
}