package model;

import main.model.Resource;
import main.model.Settler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceTest {
    @Test
    @DisplayName("Create a resource")
    void createResourceNotAffected() {
        Resource r1 = new Resource("R1");
        assertFalse(r1.isAffected());
    }

    @Test
    @DisplayName("Create a resource with empty name")
    void createResourceEmptyName() {
        Exception exception = assertThrowsExactly(IllegalArgumentException.class, () -> new Resource(""));
        assertEquals("Resource name cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Affect resource")
    void affectResourceToSettler() {
        Settler s1 = new Settler("A1", 1);
        Resource r1 = new Resource("R1");
        s1.setAffectation(r1);
        r1.setAffected(true);

        assertTrue(r1.isAffected());
        assertEquals(r1, s1.getAffectation());
    }

    @Test
    @DisplayName("Affect resource and remove affectation")
    void affectResourceToSettlerThenRemoveIt() {
        Settler s1 = new Settler("A1", 1);
        Resource r1 = new Resource("R1");

        s1.setAffectation(r1);
        r1.setAffected(true);
        assertTrue(r1.isAffected());
        assertEquals(r1, s1.getAffectation());

        s1.setAffectation(null);
        r1.setAffected(false);
        assertFalse(r1.isAffected());
        assertNull(s1.getAffectation());
    }
}
