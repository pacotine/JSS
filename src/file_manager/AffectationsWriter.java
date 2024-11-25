package file_manager;

import simulation.Simulation;
import model.Settler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A utility class for writing the simulation's affectations to a file.
 */
public class AffectationsWriter {
    private Simulation simulation;
    private File file;

    /**
     * Constructs an {@link AffectationsWriter} with the specified simulation and output file path.
     *
     * @param simulation the simulation to be saved
     * @param path       the path of the file where the affectations will be written
     * @throws IOException if the file cannot be created or accessed
     */
    public AffectationsWriter(Simulation simulation, String path) throws IOException {
        this.file = new File(path);
        this.simulation = simulation;
    }

    /**
     * Saves the simulation's settler affectations to the specified file.
     * Each line in the file represents a settler and their assigned affectation in the format:
     * <br>
     * {@code settlerName:affectation}.
     *
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public void saveSimulation() throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        for(Settler settler : simulation.getSettlers()) {
            fileWriter.append(settler.getName()).append(":").append(String.valueOf(settler.getAffectation())).append("\n");
        }

        fileWriter.close();
    }
}
