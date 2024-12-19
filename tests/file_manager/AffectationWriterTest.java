package file_manager;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import main.file_manager.AffectationsWriter;
import main.simulation.Simulation;

public class AffectationWriterTest {

    @Test
    @DisplayName("Save simulation with an incorrect (empty) path")
    public void saveSimulationButPathIsIncorrect(){
        Simulation simulation = new Simulation(10);
        String path = ""; //empty path
        assertThrows(IOException.class, ()-> {
            AffectationsWriter affectationsWriter = new AffectationsWriter(simulation, path);
            affectationsWriter.saveSimulation();
        });
    }

}
