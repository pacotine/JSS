package model;

import main.model.Resource;
import main.model.Settler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SettlerTest {
    @Test
    @DisplayName("A settler has a bad relation with another")
    void addBadRelation() {
        final int n = 2;
        Settler s1 = new Settler("A1", n);
        Settler s2 = new Settler("A2", n);
        s1.addBadRelation(s2);

        assertEquals(1, s1.getBadRelations().size());
        assertEquals(0, s2.getBadRelations().size());

        assertTrue(s1.getBadRelations().contains(s2));
        assertFalse(s2.getBadRelations().contains(s1));
    }

    @Test
    @DisplayName("A settler cannot detests his-self")
    void addBadRelationOnItSelf() {
        Settler s1 = new Settler("A1", 1);

        Exception exception = assertThrowsExactly(IllegalArgumentException.class, () -> s1.addBadRelation(s1));
        assertEquals("A settler cannot detests his-self", exception.getMessage());
    }

    @Test
    @DisplayName("Affect a resource to a settler with no preferences")
    void setAffectationAndSettlerHasNoPreferences() {
        final int n = 5;
        Resource[] preferences = new Resource[n];
        for(int i = 0; i<n; i++) {
            preferences[i] = new Resource("R"+i);
        }
        Settler s1 = new Settler("A1", n);

        Resource affectation = preferences[2];
        s1.setAffectation(affectation);

        assertEquals(affectation, s1.getAffectation());
        assertEquals(-1, s1.getAffectationRank());
    }

    @Test
    @DisplayName("Affect a resource to a settler with preferences")
    void setAffectationAndSettlerHasPreferences() {
        final int n = 5;
        Resource[] preferences = new Resource[n];
        for(int i = 0; i<n; i++) {
            preferences[i] = new Resource("R"+i);
        }
        Settler s1 = new Settler("A1", n);

        Resource affectation = preferences[2];
        s1.setPreferences(preferences);
        s1.setAffectation(affectation);

        assertEquals(affectation, s1.getAffectation());
        assertEquals(2, s1.getAffectationRank());
    }

    @Test
    @DisplayName("Affect a resource to a settler with no preferences, then he does")
    void setAffectationAndSettlerHasNoPreferencesAndThenHeDoes() {
        final int n = 5;
        Resource[] preferences = new Resource[n];
        for(int i = 0; i<n; i++) {
            preferences[i] = new Resource("R"+i);
        }
        Settler s1 = new Settler("A1", n);

        Resource affectation = preferences[2];
        s1.setAffectation(affectation);
        assertEquals(affectation, s1.getAffectation());
        assertEquals(-1, s1.getAffectationRank());

        s1.setPreferences(preferences);
        affectation = preferences[4];
        s1.setAffectation(affectation);
        assertEquals(affectation, s1.getAffectation());
        assertEquals(4, s1.getAffectationRank());
    }

    @Test
    @DisplayName("Checks whether the settler's preferences are valid (with no preferences)")
    void checkPreferencesAndSettlerHasNoPreferences() {
        final int n = 10;
        Settler s1 = new Settler("A1", n);

        assertFalse(s1.checkPreferences(n));
    }

    @Test
    @DisplayName("Checks whether the settler's preferences are valid (with duplicates)")
    void checkPreferencesAndSettlerPreferencesHaveDuplicates() {
        final int n = 10;
        Resource[] preferences = new Resource[n];
        for(int i = 0; i<n; i++) {
            preferences[i] = new Resource("R"+i);
        }
        preferences[0] = preferences[1]; //duplicates
        Settler s1 = new Settler("A1", n);
        s1.setPreferences(preferences);

        assertFalse(s1.checkPreferences(n));
    }

    @Test
    @DisplayName("Checks whether the settler's preferences are valid")
    void checkPreferencesCorrect() {
        final int n = 10;
        Resource[] preferences = new Resource[n];
        for(int i = 0; i<n; i++) {
            preferences[i] = new Resource("R"+i);
        }
        Settler s1 = new Settler("A1", n);
        s1.setPreferences(preferences);

        assertTrue(s1.checkPreferences(n));
    }
}
