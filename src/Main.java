import env.Dispatcher;
import env.Simulation;
import file_manager.ColonyReader;
import ui.MainMenu;
import ui.FileMenu;

import java.io.File;
import java.io.IOException;

/**
 * Initializes the program.
 */
public class Main {
    public static void main(String[] args) {
        if(args.length == 0) {
            MainMenu mainMenu = new MainMenu();
            mainMenu.start();
        } else if(args.length == 1) {
            String path = args[0];
            try(ColonyReader cr = new ColonyReader(new File(path))) {
                Simulation simulation = cr.initSimulation();
                FileMenu fileMenu = new FileMenu(simulation);
                fileMenu.start();
            } catch (IOException e) { //auto close
                System.out.println("Path " + path + " invalid" +
                        "\nHow to use?" +
                        "\n`java Main [path/to/colony/file.txt]` with 'file.txt' your colony file");
            }
        }
    }
}