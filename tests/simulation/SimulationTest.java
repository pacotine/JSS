package simulation;

import main.model.Settler;
import main.simulation.Simulation;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {

    @Test
    void switchAffectations() {
        Simulation simulation = new Simulation(4);
        simulation.affect("A1", "R1");
        simulation.affect("B1", "R2");
        simulation.switchAffectations("A1", "B1");
        assertEquals("R1", simulation.getSettlersMap().get("B1").getAffectation().getName());
        assertEquals("R2", simulation.getSettlersMap().get("A1").getAffectation().getName());
    }

    @Test
    void checkIfStable() {
        Simulation s1 = new Simulation(2);
        s1.setSettlerPreferences("A1", "R1", "R2");
        s1.setSettlerPreferences("B1", "R1", "R2");
        assertTrue(s1.checkIfStable());
        Simulation s2 = new Simulation(2);
        s2.setSettlerPreferences("B1", "R1", "R2");
        assertFalse(s2.checkIfStable());
    }

    @Test
    void affect() {
        Simulation simulation = new Simulation(4);
        simulation.affect("A1", "R1");
        assertEquals("R1", simulation.getSettlersMap().get("A1").getAffectation().getName());
        assertNull(simulation.getSettlersMap().get("B1").getAffectation());
    }

    @Test
    void setBadRelations() {
        Simulation simulation = new Simulation(4);
        simulation.setBadRelations("A1", "B1");
        Map<String, Settler> settlers = simulation.getSettlersMap();
        assertTrue(settlers.get("A1").getBadRelations().contains(settlers.get("B1")));
        assertTrue(settlers.get("B1").getBadRelations().contains(settlers.get("A1")));
        assertTrue(settlers.get("C1").getBadRelations().isEmpty());
    }

    @Test
    void jealousIfBadRelationHasAResourceWithHigherRank() {
        Simulation simulation = new Simulation(2);
        simulation.setSettlerPreferences("A1", "R1", "R2");
        simulation.setSettlerPreferences("B1", "R1", "R2");
        simulation.setBadRelations("A1", "B1");
        simulation.affect("A1", "R1");
        simulation.affect("B1", "R2");
        assertEquals(1, simulation.getJealousNumber());
    }

    @Test
    void noJealousIfNoBadRelations() {
        Simulation simulation = new Simulation(2);
        simulation.setSettlerPreferences("A1", "R1", "R2");
        simulation.setSettlerPreferences("B1", "R1", "R2");
        simulation.affect("A1", "R1");
        simulation.affect("B1", "R2");
        assertEquals(0, simulation.getJealousNumber());
    }

    @Test
    void jealousSettlerCountedOnlyOnce() {
        Simulation simulation = new Simulation(3);
        simulation.setSettlerPreferences("A1", "R1", "R2", "R3");
        simulation.setSettlerPreferences("B1", "R1", "R2", "R3");
        simulation.setSettlerPreferences("C1", "R1", "R2", "R3");
        simulation.setBadRelations("A1", "B1");
        simulation.setBadRelations("A1", "C1");
        simulation.affect("A1", "R3");
        simulation.affect("B1", "R1");
        simulation.affect("C1", "R2");
        assertEquals(1, simulation.getJealousNumber());
    }

    @Test
    void clearClearsAffectations() {
        Simulation simulation = new Simulation(2);
        simulation.affect("A1", "R1");
        simulation.affect("B1", "R2");
        assertNotNull(simulation.getSettlersMap().get("A1").getAffectation());
        assertNotNull(simulation.getSettlersMap().get("B1").getAffectation());
        simulation.clear();
        assertNull(simulation.getSettlersMap().get("A1").getAffectation());
        assertNull(simulation.getSettlersMap().get("B1").getAffectation());
    }
}