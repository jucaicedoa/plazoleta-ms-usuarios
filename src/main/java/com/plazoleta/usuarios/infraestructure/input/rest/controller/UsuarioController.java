package com.plazoleta.usuarios.infraestructure.input.rest.controller;

import com.plazoleta.usuarios.application.dto.CrearPropietarioDto;
import com.plazoleta.usuarios.application.dto.response.UsuarioResponseDto;
import com.plazoleta.usuarios.application.handler.IUsuarioHandler;
import com.plazoleta.usuarios.infraestructure.input.rest.dto.CrearPropietarioRequestDto;
import com.plazoleta.usuarios.infraestructure.input.rest.mapper.CrearPropietarioRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class UsuarioController {

    private final IUsuarioHandler usuarioHandler;
    private final CrearPropietarioRestMapper crearPropietarioRestMapper;

    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> obtenerUsuarioPorId(@PathVariable Integer id) {
        UsuarioResponseDto usuario = usuarioHandler.obtenerUsuarioPorId(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Crear un propietario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Propietario creado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario menor de edad", content = @Content),
            @ApiResponse(responseCode = "409", description = "Correo ya registrado", content = @Content)
    })
    @PostMapping("/propietario")
    public ResponseEntity<Void> crearPropietario(@Valid @RequestBody CrearPropietarioRequestDto requestDto) {
        CrearPropietarioDto dto = crearPropietarioRestMapper.toApplicationDto(requestDto);
        usuarioHandler.crearPropietario(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}