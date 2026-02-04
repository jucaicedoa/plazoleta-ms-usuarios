package com.plazoleta.usuarios.infraestructure.out.jpa.adapter;

import com.plazoleta.usuarios.domain.exception.CorreoYaRegistradoException;
import com.plazoleta.usuarios.domain.exception.DocumentoYaRegistradoException;
import com.plazoleta.usuarios.domain.exception.RolNoEncontradoException;
import com.plazoleta.usuarios.domain.exception.ValorExcedeLongitudException;
import com.plazoleta.usuarios.domain.model.Rol;
import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.infraestructure.out.jpa.entity.RoleEntity;
import com.plazoleta.usuarios.infraestructure.out.jpa.entity.UsuarioEntity;
import com.plazoleta.usuarios.infraestructure.out.jpa.mapper.UsuarioEntityMapper;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.RoleRepository;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UsuarioJpaAdapterTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UsuarioEntityMapper mapper;

    private UsuarioJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioJpaAdapter(usuarioRepository, roleRepository, mapper);
    }

    @Test
    void deberiaGuardarUsuarioCorrectamente() {
        // Arrange
        Usuario usuario = crearUsuario();
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setNombre("Juan");
        usuarioEntity.setApellido("Pérez");
        RoleEntity roleEntity = crearRoleEntity();

        when(mapper.toEntity(usuario)).thenReturn(usuarioEntity);
        when(roleRepository.findByName("PROPIETARIO")).thenReturn(Optional.of(roleEntity));
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioEntity);
        when(mapper.toDomain(usuarioEntity)).thenReturn(usuario);

        // Act
        Usuario resultado = adapter.guardarUsuario(usuario);

        // Assert
        assertNotNull(resultado);
        verify(mapper, times(1)).toEntity(usuario);
        verify(roleRepository, times(1)).findByName("PROPIETARIO");
        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));
        verify(mapper, times(1)).toDomain(usuarioEntity);
    }

    @Test
    void deberiaLanzarExcepcionCuandoRolNoExiste() {
        // Arrange
        Usuario usuario = crearUsuario();
        UsuarioEntity usuarioEntity = new UsuarioEntity();

        when(mapper.toEntity(usuario)).thenReturn(usuarioEntity);
        when(roleRepository.findByName("PROPIETARIO")).thenReturn(Optional.empty());

        // Act & Assert
        RolNoEncontradoException exception = assertThrows(
                RolNoEncontradoException.class,
                () -> adapter.guardarUsuario(usuario)
        );

        assertEquals("Rol PROPIETARIO no encontrado en la base de datos", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deberiaVerificarSiCorreoExiste() {
        // Arrange
        String correo = "juan@example.com";
        when(usuarioRepository.existsByCorreo(correo)).thenReturn(true);

        // Act
        boolean resultado = adapter.existeCorreo(correo);

        // Assert
        assertTrue(resultado);
        verify(usuarioRepository, times(1)).existsByCorreo(correo);
    }

    @Test
    void deberiaRetornarFalsoCuandoCorreoNoExiste() {
        // Arrange
        String correo = "noexiste@example.com";
        when(usuarioRepository.existsByCorreo(correo)).thenReturn(false);

        // Act
        boolean resultado = adapter.existeCorreo(correo);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository, times(1)).existsByCorreo(correo);
    }

    @Test
    void deberiaAsignarRolALaEntidadAntesDeGuardar() {
        // Arrange
        Usuario usuario = crearUsuario();
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        RoleEntity roleEntity = crearRoleEntity();

        when(mapper.toEntity(usuario)).thenReturn(usuarioEntity);
        when(roleRepository.findByName("PROPIETARIO")).thenReturn(Optional.of(roleEntity));
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioEntity);
        when(mapper.toDomain(usuarioEntity)).thenReturn(usuario);

        // Act
        adapter.guardarUsuario(usuario);

        // Assert
        assertEquals(roleEntity, usuarioEntity.getRole());
    }

    @Test
    void deberiaObtenerUsuarioPorIdCuandoExiste() {
        // Arrange
        Integer id = 1;
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setId(1);
        usuarioEntity.setNombre("Juan");
        usuarioEntity.setApellido("Pérez");
        usuarioEntity.setCorreo("juan@example.com");
        
        Usuario usuario = crearUsuario();
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEntity));
        when(mapper.toDomain(usuarioEntity)).thenReturn(usuario);

        // Act
        Usuario resultado = adapter.obtenerUsuarioPorId(id);

        // Assert
        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findById(1L);
        verify(mapper, times(1)).toDomain(usuarioEntity);
    }

    @Test
    void deberiaRetornarNullCuandoUsuarioNoExiste() {
        // Arrange
        Integer id = 999;
        
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Usuario resultado = adapter.obtenerUsuarioPorId(id);

        // Assert
        assertEquals(null, resultado);
        verify(usuarioRepository, times(1)).findById(999L);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void deberiaLanzarCorreoYaRegistradoExceptionCuandoHayDataIntegrityViolationEnEmail() {
        // Arrange
        Usuario usuario = crearUsuario();
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        RoleEntity roleEntity = crearRoleEntity();

        when(mapper.toEntity(usuario)).thenReturn(usuarioEntity);
        when(roleRepository.findByName("PROPIETARIO")).thenReturn(Optional.of(roleEntity));
        when(usuarioRepository.save(any(UsuarioEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint [email]"));

        // Act & Assert
        assertThrows(
                CorreoYaRegistradoException.class,
                () -> adapter.guardarUsuario(usuario)
        );

        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));
    }

    @Test
    void deberiaLanzarDocumentoYaRegistradoExceptionCuandoHayDataIntegrityViolationEnDocument() {
        // Arrange
        Usuario usuario = crearUsuario();
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        RoleEntity roleEntity = crearRoleEntity();

        when(mapper.toEntity(usuario)).thenReturn(usuarioEntity);
        when(roleRepository.findByName("PROPIETARIO")).thenReturn(Optional.of(roleEntity));
        when(usuarioRepository.save(any(UsuarioEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint [document]"));

        // Act & Assert
        assertThrows(
                DocumentoYaRegistradoException.class,
                () -> adapter.guardarUsuario(usuario)
        );

        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));
    }

    @Test
    void deberiaLanzarValorExcedeLongitudExceptionCuandoHayDataIntegrityViolationPorValorMuyLargo() {
        // Arrange
        Usuario usuario = crearUsuario();
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        RoleEntity roleEntity = crearRoleEntity();

        when(mapper.toEntity(usuario)).thenReturn(usuarioEntity);
        when(roleRepository.findByName("PROPIETARIO")).thenReturn(Optional.of(roleEntity));
        when(usuarioRepository.save(any(UsuarioEntity.class)))
                .thenThrow(new DataIntegrityViolationException("value too long for type character varying(13) [phone]"));

        // Act & Assert
        ValorExcedeLongitudException exception = assertThrows(
                ValorExcedeLongitudException.class,
                () -> adapter.guardarUsuario(usuario)
        );

        assertEquals("celular", exception.getCampo());
        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));
    }

    // Métodos auxiliares
    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setDocumento("12345678");
        usuario.setCelular("+573001234567");
        usuario.setFechaNacimiento(LocalDate.now().minusYears(25));
        usuario.setCorreo("juan@example.com");
        usuario.setClave("passwordEncriptada");
        usuario.setRol(new Rol(2, "PROPIETARIO"));
        return usuario;
    }

    private RoleEntity crearRoleEntity() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(2);
        roleEntity.setName("PROPIETARIO");
        return roleEntity;
    }
}