package file_manager;

import simulation.Simulation;
import model.Settler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AffectationsWriter {
    private Simulation simulation;
    private File file;

    public AffectationsWriter(Simulation simulation, String path) throws IOException {
        this.file = new File(path);
        this.simulation = simulation;
    }

    public void saveSimulation() throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        for(Settler settler : simulation.getSettlers()) {
            fileWriter.append(settler.getName()).append(":").append(String.valueOf(settler.getAffectation())).append("\n");
        }

        fileWriter.close();
    }
}
