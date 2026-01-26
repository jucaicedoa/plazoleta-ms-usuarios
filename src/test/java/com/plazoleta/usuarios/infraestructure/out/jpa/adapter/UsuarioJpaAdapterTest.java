package com.plazoleta.usuarios.infraestructure.out.jpa.adapter;

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
        RuntimeException exception = assertThrows(
                RuntimeException.class,
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