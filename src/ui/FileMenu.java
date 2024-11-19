package ui;

import env.Simulation;
import file_manager.AffectationsWriter;

import java.io.IOException;

public class FileMenu extends CLIMenu {
    private String srcPath;

    public FileMenu(Simulation simulation, String srcPath) {
        this.simulation = simulation;
        this.srcPath = srcPath;
    }

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
