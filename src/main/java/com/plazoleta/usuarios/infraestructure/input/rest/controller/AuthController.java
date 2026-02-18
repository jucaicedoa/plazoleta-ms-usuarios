package com.plazoleta.usuarios.infraestructure.input.rest.controller;

import com.plazoleta.usuarios.application.dto.LoginDto;
import com.plazoleta.usuarios.application.dto.response.LoginResponseDto;
import com.plazoleta.usuarios.application.handler.IAuthHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "API de login")
public class AuthController {

    private final IAuthHandler authHandler;

    @Operation(summary = "Iniciar sesión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token generado correctamente"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto request) {
        String token = authHandler.login(request);
        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}