package com.plazoleta.usuarios.infraestructure.input.rest.controller;

import com.plazoleta.usuarios.application.dto.CreateEmployeeDto;
import com.plazoleta.usuarios.application.dto.CreateOwnerDto;
import com.plazoleta.usuarios.application.dto.response.UserResponseDto;
import com.plazoleta.usuarios.application.handler.IUserHandler;
import com.plazoleta.usuarios.domain.model.TokenClaims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management API")
public class UserController {

    private final IUserHandler userHandler;

    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Integer id) {
        UserResponseDto user = userHandler.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Create owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Owner created", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data or user under age", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email already registered", content = @Content)
    })
    @PostMapping("/propietario")
    public ResponseEntity<Void> createOwner(@Valid @RequestBody CreateOwnerDto dto) {
        userHandler.createOwner(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Create employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid data or user under age", content = @Content),
            @ApiResponse(responseCode = "403", description = "Only owner with active restaurant can create employees", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email already registered", content = @Content)
    })
    @PostMapping("/empleado")
    public ResponseEntity<Void> createEmployee(
            HttpServletRequest request,
            @Valid @RequestBody CreateEmployeeDto dto) {
        TokenClaims claims = (TokenClaims) request.getAttribute("tokenClaims");
        if (claims == null || claims.getRestaurantId() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userHandler.createEmployee(dto, claims.getRestaurantId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}