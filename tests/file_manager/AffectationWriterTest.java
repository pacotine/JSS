package file_manager;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import main.file_manager.AffectationsWriter;
import main.simulation.Simulation;

public class AffectationWriterTest {

    @Test
    public void saveSimulationCorretly(){
        Simulation sim = new Simulation(10);
        String path = "\"C:\\Users\\Documents\\graph.txt\"";
        assertThrows(IOException.class, ()-> { new AffectationsWriter(sim, path) ;});
    }

}
