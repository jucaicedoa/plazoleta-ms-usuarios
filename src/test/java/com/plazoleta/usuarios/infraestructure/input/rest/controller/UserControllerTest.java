package com.plazoleta.usuarios.infraestructure.input.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.plazoleta.usuarios.application.dto.CreateEmployeeDto;
import com.plazoleta.usuarios.application.dto.CreateOwnerDto;
import com.plazoleta.usuarios.application.dto.response.UserResponseDto;
import com.plazoleta.usuarios.application.handler.IUserHandler;
import com.plazoleta.usuarios.domain.exception.InvalidFieldException;
import com.plazoleta.usuarios.domain.model.TokenClaims;
import com.plazoleta.usuarios.domain.exception.InvalidEmailException;
import com.plazoleta.usuarios.domain.exception.UserUnderAgeException;
import com.plazoleta.usuarios.infraestructure.exceptionhandler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private IUserHandler userHandler;

    @InjectMocks
    private UserController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldCreateOwnerAndReturn201() throws Exception {
        CreateOwnerDto dto = createValidOwnerDto();
        doNothing().when(userHandler).createOwner(any(CreateOwnerDto.class));

        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(userHandler, times(1)).createOwner(any(CreateOwnerDto.class));
    }

    @Test
    void shouldReturn400WhenFirstNameIsNull() throws Exception {
        CreateOwnerDto dto = createValidOwnerDto();
        dto.setFirstName(null);

        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Submitted data does not meet required validations"))
                .andExpect(jsonPath("$.errors.firstName").exists());

        verify(userHandler, never()).createOwner(any());
    }

    @Test
    void shouldReturn400WhenFirstNameIsEmpty() throws Exception {
        CreateOwnerDto dto = createValidOwnerDto();
        dto.setFirstName("");

        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists());

        verify(userHandler, never()).createOwner(any());
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        CreateOwnerDto dto = createValidOwnerDto();
        dto.setEmail("invalid-email");

        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());

        verify(userHandler, never()).createOwner(any());
    }

    @Test
    void shouldReturn400WhenDocumentNumberContainsLetters() throws Exception {
        CreateOwnerDto dto = createValidOwnerDto();
        dto.setDocumentNumber("ABC123");

        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.documentNumber").exists());

        verify(userHandler, never()).createOwner(any());
    }

    @Test
    void shouldReturn400WhenPhoneIsInvalid() throws Exception {
        CreateOwnerDto dto = createValidOwnerDto();
        dto.setPhone("invalid-phone");

        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.phone").exists());

        verify(userHandler, never()).createOwner(any());
    }

    @Test
    void shouldReturn400WhenBirthDateIsInFuture() throws Exception {
        CreateOwnerDto dto = createValidOwnerDto();
        dto.setBirthDate(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.birthDate").exists());

        verify(userHandler, never()).createOwner(any());
    }

    @Test
    void shouldReturn400WhenPasswordIsTooShort() throws Exception {
        CreateOwnerDto dto = createValidOwnerDto();
        dto.setPassword("12345");

        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());

        verify(userHandler, never()).createOwner(any());
    }

    @Test
    void shouldReturn400WhenHandlerThrowsInvalidFieldException() throws Exception {
        CreateOwnerDto dto = createValidOwnerDto();
        doThrow(new InvalidFieldException("Invalid document"))
                .when(userHandler).createOwner(any(CreateOwnerDto.class));

        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_FIELD"))
                .andExpect(jsonPath("$.message").value("Invalid document"));
    }

    @Test
    void shouldReturn400WhenHandlerThrowsInvalidEmailException() throws Exception {
        CreateOwnerDto dto = createValidOwnerDto();
        doThrow(new InvalidEmailException())
                .when(userHandler).createOwner(any(CreateOwnerDto.class));

        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_EMAIL"));
    }

    @Test
    void shouldReturn400WhenHandlerThrowsUserUnderAgeException() throws Exception {
        CreateOwnerDto dto = createValidOwnerDto();
        doThrow(new UserUnderAgeException())
                .when(userHandler).createOwner(any(CreateOwnerDto.class));

        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_AGE"));
    }

    @Test
    void shouldValidateAllRequiredFields() throws Exception {
        CreateOwnerDto dto = new CreateOwnerDto();

        mockMvc.perform(post("/api/v1/usuarios/propietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.lastName").exists())
                .andExpect(jsonPath("$.errors.documentNumber").exists())
                .andExpect(jsonPath("$.errors.phone").exists())
                .andExpect(jsonPath("$.errors.birthDate").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());

        verify(userHandler, never()).createOwner(any());
    }

    @Test
    void shouldGetUserByIdAndReturn200() throws Exception {
        Integer id = 1;
        UserResponseDto responseDto = createUserResponseDto();

        when(userHandler.findById(id)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/usuarios/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.firstName").value(responseDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(responseDto.getLastName()))
                .andExpect(jsonPath("$.documentNumber").value(responseDto.getDocumentNumber()))
                .andExpect(jsonPath("$.phone").value(responseDto.getPhone()))
                .andExpect(jsonPath("$.email").value(responseDto.getEmail()))
                .andExpect(jsonPath("$.role").value(responseDto.getRole()));

        verify(userHandler, times(1)).findById(id);
    }

    @Test
    void shouldReturn404WhenUserDoesNotExist() throws Exception {
        Integer id = 999;

        when(userHandler.findById(id)).thenReturn(null);

        mockMvc.perform(get("/api/v1/usuarios/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userHandler, times(1)).findById(id);
    }

    @Test
    void shouldCreateEmployeeAndReturn201() throws Exception {
        TokenClaims tokenClaims = new TokenClaims(1, "owner@mail.com", "PROPIETARIO", 1);
        CreateEmployeeDto dto = createValidEmployeeDto();

        doNothing().when(userHandler).createEmployee(any(CreateEmployeeDto.class), any(Integer.class));

        mockMvc.perform(post("/api/v1/usuarios/empleado")
                        .with(request -> {
                            request.setAttribute("tokenClaims", tokenClaims);
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(userHandler, times(1)).createEmployee(any(CreateEmployeeDto.class), any(Integer.class));
    }

    @Test
    void shouldReturn400WhenCreateEmployeeWithEmptyFirstName() throws Exception {
        CreateEmployeeDto dto = createValidEmployeeDto();
        dto.setFirstName("");

        mockMvc.perform(post("/api/v1/usuarios/empleado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists());

        verify(userHandler, never()).createEmployee(any(), any());
    }

    private CreateOwnerDto createValidOwnerDto() {
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

    private CreateEmployeeDto createValidEmployeeDto() {
        CreateEmployeeDto dto = new CreateEmployeeDto();
        dto.setFirstName("Pedro");
        dto.setLastName("García");
        dto.setDocumentNumber("87654321");
        dto.setPhone("+573009876543");
        dto.setBirthDate(LocalDate.now().minusYears(22));
        dto.setEmail("pedro@restaurante.com");
        dto.setPassword("empleado123");
        return dto;
    }

    private UserResponseDto createUserResponseDto() {
        return UserResponseDto.builder()
                .id(1)
                .firstName("Juan")
                .lastName("Pérez")
                .documentNumber("12345678")
                .phone("+573001234567")
                .birthDate(LocalDate.now().minusYears(25))
                .email("juan@example.com")
                .role("PROPIETARIO")
                .build();
    }
}