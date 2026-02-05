package com.plazoleta.usuarios.infraestructure.input.rest.mapper;

import com.plazoleta.usuarios.application.dto.CrearEmpleadoDto;
import com.plazoleta.usuarios.infraestructure.input.rest.dto.CrearEmpleadoRequestDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CrearEmpleadoRestMapperTest {

    private final CrearEmpleadoRestMapper mapper = Mappers.getMapper(CrearEmpleadoRestMapper.class);

    @Test
    void deberiaMapearRequestDtoAApplicationDto() {
        // Arrange
        CrearEmpleadoRequestDto requestDto = new CrearEmpleadoRequestDto();
        requestDto.setNombre("Pedro");
        requestDto.setApellido("García");
        requestDto.setDocumento("87654321");
        requestDto.setCelular("+573009876543");
        requestDto.setFechaNacimiento(LocalDate.now().minusYears(22));
        requestDto.setCorreo("pedro@restaurante.com");
        requestDto.setClave("empleado123");

        // Act
        CrearEmpleadoDto applicationDto = mapper.toApplicationDto(requestDto);

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
        CrearEmpleadoDto applicationDto = mapper.toApplicationDto(null);

        // Assert
        assertEquals(null, applicationDto);
    }
}