package com.plazoleta.usuarios.infraestructure.input.rest.controller;

import com.plazoleta.usuarios.application.dto.CrearPropietarioDto;
import com.plazoleta.usuarios.infraestructure.input.rest.dto.CrearPropietarioRequestDto;
import com.plazoleta.usuarios.infraestructure.input.rest.mapper.CrearPropietarioRestMapper;
import com.plazoleta.usuarios.application.handler.CrearPropietarioHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class UsuarioController {

    private final CrearPropietarioHandler handler;
    private final CrearPropietarioRestMapper restMapper;

    @PostMapping("/propietario")
    @Operation(
            summary = "Crear un propietario",
            description = "Crea una cuenta de usuario con rol de propietario. El propietario debe ser mayor de edad y proporcionar todos los campos obligatorios."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Propietario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario menor de edad"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Map<String, Object>> crearPropietario(@Valid @RequestBody CrearPropietarioRequestDto requestDto) {
        CrearPropietarioDto dto = restMapper.toApplicationDto(requestDto);
        handler.crearPropietario(dto);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("codigo", "PROPIETARIO_CREADO");
        response.put("mensaje", "El propietario ha sido creado exitosamente");
        response.put("datos", Map.of(
                "correo", requestDto.getCorreo(),
                "nombre", requestDto.getNombre() + " " + requestDto.getApellido(),
                "documento", requestDto.getDocumento()
        ));
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}