package com.plazoleta.usuarios.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleTest {

    @Test
    void shouldCreateRoleWithFullConstructor() {
        Role role = new Role(1, "ADMINISTRADOR");

        assertNotNull(role);
        assertEquals(1, role.getId());
        assertEquals("ADMINISTRADOR", role.getName());
    }

    @Test
    void shouldCreateRoleWithEmptyConstructor() {
        Role role = new Role();

        assertNotNull(role);
        assertNull(role.getName());
    }

    @Test
    void shouldAllowModifyingAttributesWithSetters() {
        Role role = new Role();

        role.setId(2);
        role.setName("PROPIETARIO");

        assertEquals(2, role.getId());
        assertEquals("PROPIETARIO", role.getName());
    }

    @Test
    void shouldImplementEqualsAndHashCodeCorrectly() {
        Role role1 = new Role(1, "ADMINISTRADOR");
        Role role2 = new Role(1, "ADMINISTRADOR");
        Role role3 = new Role(2, "PROPIETARIO");

        assertEquals(role1, role2);
        assertNotEquals(role1, role3);
        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        Role role = new Role(1, "ADMINISTRADOR");

        String result = role.toString();

        assertNotNull(result);
        assertTrue(result.contains("1"));
        assertTrue(result.contains("ADMINISTRADOR"));
    }
}