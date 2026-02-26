package com.plazoleta.usuarios.infraestructure.out.validation;

import com.plazoleta.usuarios.domain.exception.InvalidEmailException;
import com.plazoleta.usuarios.domain.exception.InvalidFieldException;
import com.plazoleta.usuarios.domain.exception.UserUnderAgeException;
import com.plazoleta.usuarios.domain.model.UserCreationData;
import com.plazoleta.usuarios.domain.spi.UserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCreationValidationAdapterTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    private UserCreationValidationAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserCreationValidationAdapter(userPersistencePort);
    }

    @Test
    void shouldNotThrowWhenDataIsValid() {
        UserCreationData data = validData();
        when(userPersistencePort.existsByEmail(data.getEmail())).thenReturn(false);

        assertDoesNotThrow(() -> adapter.validate(data));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123ABC", "12.34", ""})
    void shouldThrowInvalidFieldWhenDocumentIsInvalid(String invalidDocument) {
        UserCreationData data = buildData(invalidDocument, "juan@example.com", "+573001234567", LocalDate.now().minusYears(25));

        InvalidFieldException ex = assertThrows(InvalidFieldException.class, () -> adapter.validate(data));
        assertEquals("Invalid document", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "correo@", "@domain.com", "a@b"})
    void shouldThrowInvalidEmailWhenEmailFormatIsInvalid(String invalidEmail) {
        UserCreationData data = buildData("12345678", invalidEmail, "+573001234567", LocalDate.now().minusYears(25));

        assertThrows(InvalidEmailException.class, () -> adapter.validate(data));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc123", "123-456", ""})
    void shouldThrowInvalidFieldWhenPhoneIsInvalid(String invalidPhone) {
        UserCreationData data = buildData("12345678", "juan@example.com", invalidPhone, LocalDate.now().minusYears(25));

        InvalidFieldException ex = assertThrows(InvalidFieldException.class, () -> adapter.validate(data));
        assertEquals("Invalid phone", ex.getMessage());
    }

    @Test
    void shouldThrowUserUnderAgeWhenBirthDateIsNull() {
        UserCreationData data = buildData("12345678", "juan@example.com", "+573001234567", null);

        assertThrows(UserUnderAgeException.class, () -> adapter.validate(data));
    }

    @Test
    void shouldThrowUserUnderAgeWhenUserIsUnder18() {
        UserCreationData data = buildData("12345678", "juan@example.com", "+573001234567", LocalDate.now().minusYears(17));

        assertThrows(UserUnderAgeException.class, () -> adapter.validate(data));
    }

    @Test
    void shouldThrowUserUnderAgeWhenUserIsExactly17() {
        UserCreationData data = buildData("12345678", "juan@example.com", "+573001234567", LocalDate.now().minusYears(17).minusDays(1));

        assertThrows(UserUnderAgeException.class, () -> adapter.validate(data));
    }

    @Test
    void shouldNotThrowWhenUserIsExactly18() {
        UserCreationData data = buildData("12345678", "juan@example.com", "+573001234567", LocalDate.now().minusYears(18));
        when(userPersistencePort.existsByEmail("juan@example.com")).thenReturn(false);

        assertDoesNotThrow(() -> adapter.validate(data));
    }

    @Test
    void shouldThrowInvalidFieldWhenEmailAlreadyExists() {
        UserCreationData data = validData();
        when(userPersistencePort.existsByEmail(data.getEmail())).thenReturn(true);

        InvalidFieldException ex = assertThrows(InvalidFieldException.class, () -> adapter.validate(data));
        assertEquals("Email already registered", ex.getMessage());
    }

    private UserCreationData validData() {
        return buildData("12345678", "juan@example.com", "+573001234567", LocalDate.now().minusYears(25));
    }

    private UserCreationData buildData(String documentNumber, String email, String phone, LocalDate birthDate) {
        return UserCreationData.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .documentNumber(documentNumber)
                .phone(phone)
                .birthDate(birthDate)
                .email(email)
                .password("password123")
                .restaurantId(1)
                .build();
    }
}