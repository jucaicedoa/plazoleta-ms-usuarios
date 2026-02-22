package com.plazoleta.usuarios.domain.usecase;

import com.plazoleta.usuarios.domain.exception.EmailAlreadyRegisteredException;
import com.plazoleta.usuarios.domain.exception.InvalidFieldException;
import com.plazoleta.usuarios.domain.exception.InvalidEmailException;
import com.plazoleta.usuarios.domain.exception.UserUnderAgeException;
import com.plazoleta.usuarios.domain.model.User;
import com.plazoleta.usuarios.domain.model.UserCreationData;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserPersistencePort persistencePort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private UserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UserUseCase(persistencePort, passwordEncoderPort);
    }

    @Test
    void shouldCreateOwnerWithValidData() {
        UserCreationData data = createValidData();

        when(persistencePort.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPassword");
        when(persistencePort.saveUser(any(User.class))).thenReturn(new User());

        useCase.createOwner(data);

        verify(persistencePort, times(1)).existsByEmail(data.getEmail());
        verify(passwordEncoderPort, times(1)).encode("password123");
        verify(persistencePort, times(1)).saveUser(any(User.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123ABC456", ""})
    void shouldThrowWhenDocumentIsInvalid(String invalidDocument) {
        UserCreationData data = UserCreationData.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .documentNumber(invalidDocument)
                .phone("+573001234567")
                .birthDate(LocalDate.now().minusYears(25))
                .email("juan@example.com")
                .password("password123")
                .restaurantId(null)
                .build();

        InvalidFieldException exception = assertThrows(
                InvalidFieldException.class,
                () -> useCase.createOwner(data)
        );

        assertEquals("Invalid document", exception.getMessage());
        verify(persistencePort, never()).saveUser(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "correo@"})
    void shouldThrowWhenEmailIsInvalid(String invalidEmail) {
        UserCreationData data = UserCreationData.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .documentNumber("12345678")
                .phone("+573001234567")
                .birthDate(LocalDate.now().minusYears(25))
                .email(invalidEmail)
                .password("password123")
                .restaurantId(null)
                .build();

        assertThrows(InvalidEmailException.class, () -> useCase.createOwner(data));
        verify(persistencePort, never()).saveUser(any());
    }

    @Test
    void shouldThrowWhenUserIsUnder18() {
        UserCreationData data = UserCreationData.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .documentNumber("12345678")
                .phone("+573001234567")
                .birthDate(LocalDate.now().minusYears(17))
                .email("juan@example.com")
                .password("password123")
                .restaurantId(null)
                .build();

        UserUnderAgeException exception = assertThrows(
                UserUnderAgeException.class,
                () -> useCase.createOwner(data)
        );

        assertTrue(exception.getMessage().contains("18"));
        verify(persistencePort, never()).saveUser(any());
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        UserCreationData data = createValidData();

        when(persistencePort.existsByEmail(anyString())).thenReturn(true);

        EmailAlreadyRegisteredException exception = assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> useCase.createOwner(data)
        );

        assertEquals("Email already registered", exception.getMessage());
        verify(persistencePort, times(1)).existsByEmail(data.getEmail());
        verify(persistencePort, never()).saveUser(any());
    }

    @Test
    void shouldEncodePasswordBeforeSaving() {
        UserCreationData data = createValidData();
        String originalPassword = data.getPassword();
        String encodedPassword = "encodedPassword";
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(persistencePort.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(originalPassword)).thenReturn(encodedPassword);
        when(persistencePort.saveUser(any(User.class))).thenReturn(new User());

        useCase.createOwner(data);

        verify(passwordEncoderPort, times(1)).encode(originalPassword);
        verify(persistencePort).saveUser(userCaptor.capture());
        assertEquals(encodedPassword, userCaptor.getValue().getPassword());
    }

    @Test
    void shouldAssignOwnerRoleWhenCreating() {
        UserCreationData data = createValidData();
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(persistencePort.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPassword");
        when(persistencePort.saveUser(any(User.class))).thenReturn(new User());

        useCase.createOwner(data);

        verify(persistencePort).saveUser(userCaptor.capture());
        User user = userCaptor.getValue();
        assertNotNull(user.getRole());
        assertEquals("PROPIETARIO", user.getRole().getName());
    }

    @Test
    void shouldCreateEmployeeWithValidData() {
        UserCreationData data = createValidData();

        when(persistencePort.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPassword");
        when(persistencePort.saveUser(any(User.class))).thenReturn(new User());

        useCase.createEmployee(data);

        verify(persistencePort, times(1)).existsByEmail(data.getEmail());
        verify(persistencePort, times(1)).saveUser(any(User.class));
    }

    @Test
    void shouldAssignEmployeeRoleWhenCreating() {
        UserCreationData data = createValidData();
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(persistencePort.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPassword");
        when(persistencePort.saveUser(any(User.class))).thenReturn(new User());

        useCase.createEmployee(data);

        verify(persistencePort).saveUser(userCaptor.capture());
        User user = userCaptor.getValue();
        assertNotNull(user.getRole());
        assertEquals("EMPLEADO", user.getRole().getName());
    }

    @Test
    void shouldReturnUserWhenFindById() {
        Integer id = 1;
        User user = new User();
        user.setId(id);
        when(persistencePort.findById(id)).thenReturn(user);

        User result = useCase.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(persistencePort, times(1)).findById(id);
    }

    private UserCreationData createValidData() {
        return UserCreationData.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .documentNumber("12345678")
                .phone("+573001234567")
                .birthDate(LocalDate.now().minusYears(25))
                .email("juan@example.com")
                .password("password123")
                .restaurantId(1)
                .build();
    }
}