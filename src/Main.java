import env.Dispatcher;
import env.Simulation;
import file_manager.ColonyReader;
import ui.CLIMenu;

import java.io.File;
import java.io.IOException;

/**
 * Initializes the program.
 */
public class Main {
    public static void main(String[] args) {
        if(args.length == 0) {
            CLIMenu cliMenu = new CLIMenu();
            cliMenu.start();
        } else if(args.length == 1) {
            String path = args[0];
            try(ColonyReader cr = new ColonyReader(new File(path))) {
                Simulation simulation = cr.initSimulation();
                simulation.showSettlers();
                Dispatcher dispatcher = new Dispatcher(simulation);
                dispatcher.maxLEFDispatch(5);
                simulation.showSettlers();
            } catch (IOException e) { //auto close
                System.out.println("Path " + path + " invalid" +
                        "\nHow to use?" +
                        "\n`java Main [path/to/colony/file.txt]` with 'file.txt' your colony file");
            }
        }
    }
}