package ui;

import simulation.Simulation;
import file_manager.AffectationsWriter;

import java.io.IOException;

/**
 * This class manages interaction between the user and the simulation of resource allocation to a colony,
 * defined by a file with precise syntax (see {@link file_manager.ColonyReader}).
 */
public class FileMenu extends CLIMenu {
    private String srcPath;

    /**
     * Constructs a new {@link FileMenu} with the specified simulation and source path to colony file.
     * @param simulation a fully defined simulation
     * @param srcPath the source path to colony file
     */
    public FileMenu(Simulation simulation, String srcPath) {
        this.simulation = simulation;
        this.srcPath = srcPath;
        simulation.showSettlers();
    }

    /**
     * Displays the main menu.
     * @see CLIMenu#display()
     */
    @Override
    protected void display() {
        System.out.println("Please select an option : " +
                "\n\t1. resolution" +
                "\n\t2. save yours affectations"
        );
        String res = reader.readInput();
        switch(res) {
            case "1":
                simulation.clear();
                showDispatcherMenu();
                break;
            case "2":
                showSaveMenu();
                break;
            default:
                System.out.println("Incorrect input : " + res);
                break;
        }
    }

    /**
     * Prompts the user for a file name and uses an {@link AffectationsWriter} to save the current affectation.
     */
    private void showSaveMenu() {
        boolean correct;
        do {
            correct = true;
            System.out.println("File name?");
            String destPath = reader.readInput();
            try {
                AffectationsWriter affectationsWriter = new AffectationsWriter(simulation, destPath);
                if(srcPath.equals(destPath)) throw new IllegalArgumentException("File already exists, choose another file name");
                affectationsWriter.saveSimulation();
            } catch(IllegalArgumentException | IOException il) {
                System.out.println(il.getMessage());
                correct = false;
            }
        }while(!correct);
    }
}
