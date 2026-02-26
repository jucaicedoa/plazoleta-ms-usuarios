package com.plazoleta.usuarios.infraestructure.out.jpa.adapter;

import com.plazoleta.usuarios.domain.exception.EmailAlreadyRegisteredException;
import com.plazoleta.usuarios.domain.exception.RoleNotFoundException;
import com.plazoleta.usuarios.domain.model.Role;
import com.plazoleta.usuarios.domain.model.User;
import com.plazoleta.usuarios.infraestructure.out.jpa.entity.RoleEntity;
import com.plazoleta.usuarios.infraestructure.out.jpa.entity.UserEntity;
import com.plazoleta.usuarios.infraestructure.out.jpa.mapper.UserEntityMapper;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.RoleRepository;
import com.plazoleta.usuarios.infraestructure.out.jpa.repository.UserRepository;
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
class UserJpaAdapterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserEntityMapper mapper;

    private UserJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserJpaAdapter(userRepository, roleRepository, mapper);
    }

    @Test
    void shouldSaveUserCorrectly() {
        User user = createUser();
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("Juan");
        userEntity.setLastName("Pérez");
        RoleEntity roleEntity = createRoleEntity();

        when(mapper.toEntity(user)).thenReturn(userEntity);
        when(roleRepository.findByName("PROPIETARIO")).thenReturn(Optional.of(roleEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(mapper.toDomain(userEntity)).thenReturn(user);

        User result = adapter.saveUser(user);

        assertNotNull(result);
        verify(mapper, times(1)).toEntity(user);
        verify(roleRepository, times(1)).findByName("PROPIETARIO");
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(mapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldThrowWhenRoleDoesNotExist() {
        User user = createUser();
        UserEntity userEntity = new UserEntity();

        when(mapper.toEntity(user)).thenReturn(userEntity);
        when(roleRepository.findByName("PROPIETARIO")).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> adapter.saveUser(user)
        );

        assertEquals("Role PROPIETARIO not found in database", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldCheckIfEmailExists() {
        String email = "juan@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = adapter.existsByEmail(email);

        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        String email = "noexiste@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        boolean result = adapter.existsByEmail(email);

        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void shouldAssignRoleToEntityBeforeSaving() {
        User user = createUser();
        UserEntity userEntity = new UserEntity();
        RoleEntity roleEntity = createRoleEntity();

        when(mapper.toEntity(user)).thenReturn(userEntity);
        when(roleRepository.findByName("PROPIETARIO")).thenReturn(Optional.of(roleEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(mapper.toDomain(userEntity)).thenReturn(user);

        adapter.saveUser(user);

        assertEquals(roleEntity, userEntity.getRole());
    }

    @Test
    void shouldGetUserByIdWhenExists() {
        Integer id = 1;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setFirstName("Juan");
        userEntity.setLastName("Pérez");
        userEntity.setEmail("juan@example.com");

        User user = createUser();

        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(user);

        User result = adapter.findById(id);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1);
        verify(mapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldReturnNullWhenUserDoesNotExist() {
        Integer id = 999;

        when(userRepository.findById(999)).thenReturn(Optional.empty());

        User result = adapter.findById(id);

        assertEquals(null, result);
        verify(userRepository, times(1)).findById(999);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void shouldThrowEmailAlreadyRegisteredWhenDataIntegrityViolationOnEmail() {
        User user = createUser();
        UserEntity userEntity = new UserEntity();
        RoleEntity roleEntity = createRoleEntity();

        when(mapper.toEntity(user)).thenReturn(userEntity);
        when(roleRepository.findByName("PROPIETARIO")).thenReturn(Optional.of(roleEntity));
        when(userRepository.save(any(UserEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint [email]"));

        assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> adapter.saveUser(user)
        );

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void shouldFindUserByEmailWhenExists() {
        String email = "juan@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setEmail(email);
        User user = createUser();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(user);

        Optional<User> result = adapter.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository, times(1)).findByEmail(email);
        verify(mapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldReturnEmptyWhenFindByEmailFindsNothing() {
        String email = "noexiste@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = adapter.findByEmail(email);

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByEmail(email);
        verify(mapper, never()).toDomain(any());
    }

    private User createUser() {
        User user = new User();
        user.setFirstName("Juan");
        user.setLastName("Pérez");
        user.setDocumentNumber("12345678");
        user.setPhone("+573001234567");
        user.setBirthDate(LocalDate.now().minusYears(25));
        user.setEmail("juan@example.com");
        user.setPassword("encodedPassword");
        user.setRole(new Role(2, "PROPIETARIO"));
        return user;
    }

    private RoleEntity createRoleEntity() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(2);
        roleEntity.setName("PROPIETARIO");
        return roleEntity;
    }
}