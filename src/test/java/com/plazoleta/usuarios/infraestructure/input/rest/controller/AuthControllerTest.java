package com.plazoleta.usuarios.infraestructure.input.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.usuarios.domain.api.AuthServicePort;
import com.plazoleta.usuarios.domain.exception.CredencialesInvalidasException;
import com.plazoleta.usuarios.infraestructure.exceptionhandler.GlobalExceptionHandler;
import com.plazoleta.usuarios.infraestructure.input.rest.dto.LoginRequestDto;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthServicePort authServicePort;

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
    void deberiaRetornar200YTokenCuandoLoginEsExitoso() throws Exception {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setCorreo("usuario@mail.com");
        request.setClave("clave123");
        String tokenEsperado = "eyJhbGciOiJIUzI1NiJ9...";

        when(authServicePort.login("usuario@mail.com", "clave123", null)).thenReturn(tokenEsperado);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(tokenEsperado));
    }

    @Test
    void deberiaRetornar401CuandoCredencialesSonInvalidas() throws Exception {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setCorreo("usuario@mail.com");
        request.setClave("claveIncorrecta");

        when(authServicePort.login(anyString(), anyString(), any()))
                .thenThrow(new CredencialesInvalidasException("Credenciales inválidas"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("CREDENCIALES_INVALIDAS"))
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }

    @Test
    void deberiaRetornar400CuandoCorreoEsVacio() throws Exception {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setCorreo("");
        request.setClave("clave123");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.correo").exists());
    }

    @Test
    void deberiaRetornar400CuandoClaveEsVacia() throws Exception {
        // Arrange
        LoginRequestDto request = new LoginRequestDto();
        request.setCorreo("usuario@mail.com");
        request.setClave("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.clave").exists());
    }
}