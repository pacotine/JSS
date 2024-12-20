package main.simulation;

import main.model.Settler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DispatcherTest {

    private final int n = 26, d = 5;

    @Test
    @DisplayName("Linear dispatch affects resources")
    public void linearDispatchEverySettler() {
        Simulation simulation = Simulation.random(n, d);
        Dispatcher dispatcher = new Dispatcher(simulation);
        dispatcher.linearDispatch();
        for(Settler settler : simulation.getSettlers()) {
            assertNotNull(settler.getAffectation());
        }
    }

    @Test
    @DisplayName("Switch dispatch affects resources")
    public void switchDispatchEverySettler() {
        Simulation simulation = Simulation.random(n, d);
        Dispatcher dispatcher = new Dispatcher(simulation);
        dispatcher.switchDispatch(n);
        for(Settler settler : simulation.getSettlers()) {
            assertNotNull(settler.getAffectation());
        }
    }

    @Test
    @DisplayName("MAX-LEF dispatch affects resources")
    public void maxLEFDispatchEverySettler() {
        Simulation simulation = Simulation.random(n, d);
        Dispatcher dispatcher = new Dispatcher(simulation);
        dispatcher.maxLEFDispatch(n);
        for(Settler settler : simulation.getSettlers()) {
            assertNotNull(settler.getAffectation());
        }
    }
}