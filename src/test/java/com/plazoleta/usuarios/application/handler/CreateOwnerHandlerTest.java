package com.plazoleta.usuarios.application.handler;

import com.plazoleta.usuarios.application.dto.CreateEmployeeDto;
import com.plazoleta.usuarios.application.dto.CreateOwnerDto;
import com.plazoleta.usuarios.application.dto.response.UserResponseDto;
import com.plazoleta.usuarios.application.mapper.UserRequestMapper;
import com.plazoleta.usuarios.application.mapper.UserResponseMapper;
import com.plazoleta.usuarios.domain.api.UserServicePort;
import com.plazoleta.usuarios.domain.model.User;
import com.plazoleta.usuarios.domain.model.UserCreationData;
import com.plazoleta.usuarios.domain.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CreateOwnerHandlerTest {

    @Mock
    private UserServicePort userServicePort;

    @Mock
    private UserRequestMapper mapper;

    @Mock
    private UserResponseMapper responseMapper;

    private CreateOwnerHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CreateOwnerHandler(userServicePort, mapper, responseMapper);
    }

    @Test
    void shouldCallServicePortToCreateOwner() {
        CreateOwnerDto dto = createOwnerDto();
        UserCreationData data = createUserCreationData();

        when(mapper.toUserCreationData(dto)).thenReturn(data);
        doNothing().when(userServicePort).createOwner(any(UserCreationData.class));

        handler.createOwner(dto);

        verify(mapper, times(1)).toUserCreationData(dto);
        verify(userServicePort, times(1)).createOwner(data);
    }

    @Test
    void shouldMapDtoCorrectly() {
        CreateOwnerDto dto = createOwnerDto();
        UserCreationData data = createUserCreationData();

        when(mapper.toUserCreationData(dto)).thenReturn(data);

        handler.createOwner(dto);

        verify(mapper, times(1)).toUserCreationData(dto);
    }

    @Test
    void shouldPropagateExceptionFromServicePort() {
        CreateOwnerDto dto = createOwnerDto();
        UserCreationData data = createUserCreationData();

        when(mapper.toUserCreationData(dto)).thenReturn(data);
        doThrow(new RuntimeException("Error creating owner"))
                .when(userServicePort).createOwner(any(UserCreationData.class));

        try {
            handler.createOwner(dto);
        } catch (RuntimeException e) {
            // Expected
        }

        verify(userServicePort, times(1)).createOwner(data);
    }

    @Test
    void shouldGetUserByIdAndReturnDto() {
        Integer id = 1;
        User user = createUser();
        UserResponseDto responseDto = UserResponseDto.builder()
                .id(1)
                .firstName("Juan")
                .lastName("Pérez")
                .documentNumber("12345678")
                .phone("+573001234567")
                .birthDate(LocalDate.now().minusYears(25))
                .email("juan@example.com")
                .role("PROPIETARIO")
                .build();

        when(userServicePort.findById(id)).thenReturn(user);
        when(responseMapper.toResponse(user)).thenReturn(responseDto);

        UserResponseDto result = handler.findById(id);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Juan", result.getFirstName());
        assertEquals("PROPIETARIO", result.getRole());
        verify(userServicePort, times(1)).findById(id);
        verify(responseMapper, times(1)).toResponse(user);
    }

    @Test
    void shouldReturnNullWhenUserDoesNotExist() {
        Integer id = 999;
        when(userServicePort.findById(id)).thenReturn(null);

        UserResponseDto result = handler.findById(id);

        assertNull(result);
        verify(userServicePort, times(1)).findById(id);
        verify(responseMapper, times(0)).toResponse(any());
    }

    @Test
    void shouldCallServicePortToCreateEmployee() {
        CreateEmployeeDto dto = createEmployeeDto();
        Integer restaurantId = 1;
        UserCreationData data = createUserCreationData();

        when(mapper.toUserCreationData(eq(dto), eq(restaurantId))).thenReturn(data);
        doNothing().when(userServicePort).createEmployee(any(UserCreationData.class));

        handler.createEmployee(dto, restaurantId);

        verify(mapper, times(1)).toUserCreationData(dto, restaurantId);
        verify(userServicePort, times(1)).createEmployee(data);
    }

    @Test
    void shouldPropagateExceptionFromServicePortOnCreateEmployee() {
        CreateEmployeeDto dto = createEmployeeDto();
        Integer restaurantId = 1;
        UserCreationData data = createUserCreationData();

        when(mapper.toUserCreationData(eq(dto), eq(restaurantId))).thenReturn(data);
        doThrow(new RuntimeException("Error creating employee"))
                .when(userServicePort).createEmployee(any(UserCreationData.class));

        assertThrows(RuntimeException.class, () -> handler.createEmployee(dto, restaurantId));
        verify(userServicePort, times(1)).createEmployee(data);
    }

    private CreateOwnerDto createOwnerDto() {
        CreateOwnerDto dto = new CreateOwnerDto();
        dto.setFirstName("Juan");
        dto.setLastName("Pérez");
        dto.setDocumentNumber("12345678");
        dto.setPhone("+573001234567");
        dto.setBirthDate(LocalDate.now().minusYears(25));
        dto.setEmail("juan@example.com");
        dto.setPassword("password123");
        return dto;
    }

    private UserCreationData createUserCreationData() {
        return UserCreationData.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .documentNumber("12345678")
                .phone("+573001234567")
                .birthDate(LocalDate.now().minusYears(25))
                .email("juan@example.com")
                .password("password123")
                .restaurantId(null)
                .build();
    }

    private CreateEmployeeDto createEmployeeDto() {
        CreateEmployeeDto dto = new CreateEmployeeDto();
        dto.setFirstName("Pedro");
        dto.setLastName("García");
        dto.setDocumentNumber("87654321");
        dto.setPhone("+573009876543");
        dto.setBirthDate(LocalDate.now().minusYears(22));
        dto.setEmail("pedro@restaurante.com");
        dto.setPassword("empleado123");
        dto.setRestaurantId(1);
        return dto;
    }

    private User createUser() {
        User user = new User();
        user.setId(1);
        user.setFirstName("Juan");
        user.setLastName("Pérez");
        user.setDocumentNumber("12345678");
        user.setPhone("+573001234567");
        user.setBirthDate(LocalDate.now().minusYears(25));
        user.setEmail("juan@example.com");
        user.setRole(new Role(2, "PROPIETARIO"));
        return user;
    }
}