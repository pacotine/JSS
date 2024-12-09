package test.simulation;

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
        s2.setSettlerPreferences("A1", "R1", "R2");
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
    void getJealousNumber() {
        Simulation s1 = new Simulation(2);
        s1.setSettlerPreferences("A1", "R1", "R2");
        s1.setSettlerPreferences("B1", "R1", "R2");
        s1.setBadRelations("A1", "B1");
        s1.affect("A1", "R1");
        s1.affect("B1", "R2");
        assertEquals(1, s1.getJealousNumber());

        Simulation s2 = new Simulation(2);
        s2.setSettlerPreferences("A1", "R1", "R2");
        s2.setSettlerPreferences("B1", "R1", "R2");
        s2.affect("A1", "R1");
        s2.affect("B1", "R2");
        assertEquals(0, s2.getJealousNumber());

        Simulation s3 = new Simulation(3);
        s3.setSettlerPreferences("A1", "R1", "R2", "R3");
        s3.setSettlerPreferences("B1", "R1", "R2", "R3");
        s3.setSettlerPreferences("C1", "R1", "R2", "R3");
        s3.setBadRelations("A1", "B1");
        s3.setBadRelations("A1", "C1");
        s3.affect("A1", "R3");
        s3.affect("B1", "R1");
        s3.affect("C1", "R2");
        assertEquals(1, s3.getJealousNumber());
    }
}