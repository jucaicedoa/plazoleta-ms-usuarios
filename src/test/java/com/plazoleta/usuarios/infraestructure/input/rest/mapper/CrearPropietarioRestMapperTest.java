package com.plazoleta.usuarios.infraestructure.input.rest.mapper;

import com.plazoleta.usuarios.application.dto.CrearPropietarioDto;
import com.plazoleta.usuarios.infraestructure.input.rest.dto.CrearPropietarioRequestDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CrearPropietarioRestMapperTest {

    private final CrearPropietarioRestMapper mapper = Mappers.getMapper(CrearPropietarioRestMapper.class);

    @Test
    void deberiaMapearRequestDtoAApplicationDto() {
        // Arrange
        CrearPropietarioRequestDto requestDto = new CrearPropietarioRequestDto();
        requestDto.setNombre("Juan");
        requestDto.setApellido("Pérez");
        requestDto.setDocumento("12345678");
        requestDto.setCelular("+573001234567");
        requestDto.setFechaNacimiento(LocalDate.now().minusYears(25));
        requestDto.setCorreo("juan@example.com");
        requestDto.setClave("password123");

        // Act
        CrearPropietarioDto applicationDto = mapper.toApplicationDto(requestDto);

        // Assert
        assertNotNull(applicationDto);
        assertEquals(requestDto.getNombre(), applicationDto.getNombre());
        assertEquals(requestDto.getApellido(), applicationDto.getApellido());
        assertEquals(requestDto.getDocumento(), applicationDto.getDocumento());
        assertEquals(requestDto.getCelular(), applicationDto.getCelular());
        assertEquals(requestDto.getFechaNacimiento(), applicationDto.getFechaNacimiento());
        assertEquals(requestDto.getCorreo(), applicationDto.getCorreo());
        assertEquals(requestDto.getClave(), applicationDto.getClave());
    }

    @Test
    void deberiaMapearRequestDtoNuloANull() {
        // Act
        CrearPropietarioDto applicationDto = mapper.toApplicationDto(null);

        // Assert
        assertEquals(null, applicationDto);
    }
}