package com.plazoleta.usuarios.infraestructure.input.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.usuarios.application.handler.IAuthHandler;
import com.plazoleta.usuarios.application.dto.LoginDto;
import com.plazoleta.usuarios.domain.exception.InvalidCredentialsException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IAuthHandler authHandler;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturn200AndTokenWhenLoginSucceeds() throws Exception {
        LoginDto request = new LoginDto();
        request.setEmail("usuario@mail.com");
        request.setPassword("clave123");
        String expectedToken = "eyJhbGciOiJIUzI1NiJ9...";

        when(authHandler.login(any(LoginDto.class))).thenReturn(expectedToken);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken));
    }

    @Test
    void shouldReturn401WhenCredentialsAreInvalid() throws Exception {
        LoginDto request = new LoginDto();
        request.setEmail("usuario@mail.com");
        request.setPassword("claveIncorrecta");

        when(authHandler.login(any(LoginDto.class)))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void shouldReturn400WhenEmailIsEmpty() throws Exception {
        LoginDto request = new LoginDto();
        request.setEmail("");
        request.setPassword("clave123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void shouldReturn400WhenPasswordIsEmpty() throws Exception {
        LoginDto request = new LoginDto();
        request.setEmail("usuario@mail.com");
        request.setPassword("");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}