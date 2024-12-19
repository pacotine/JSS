package main.simulation;

import main.model.Settler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DispatcherTest {

    @Test
    void linearDispatchAffectForEverySettler() {
        Simulation simulation = Simulation.random(26, 5);
        Dispatcher dispatcher = new Dispatcher(simulation);
        dispatcher.linearDispatch();
        for(Settler settler : simulation.getSettlers()) {
            assertNotNull(settler.getAffectation());
        }
    }

    @Test
    void maxLEFDispatchAffectForEverySettler() {
        Simulation simulation = Simulation.random(26, 5);
        Dispatcher dispatcher = new Dispatcher(simulation);
        dispatcher.maxLEFDispatch(10);
        for(Settler settler : simulation.getSettlers()) {
            assertNotNull(settler.getAffectation());
        }
    }
}