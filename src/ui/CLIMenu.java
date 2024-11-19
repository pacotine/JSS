package ui;

import env.Simulation;
import file_manager.AffectationsWriter;

/**
 * This class manages interaction between the user and the simulation of resource allocation to a colony.
 * This is an interactive menu which the user can quit at any time by pressing {@code quit}.
 */
public abstract class CLIMenu {
    protected final CLIReader reader;
    protected Simulation simulation;

    /**
     * Constructs a {@link CLIMenu} by initializing a {@link CLIReader}.
     */
    public CLIMenu() {
        this.reader = new CLIReader();
    }

    public void start() {
        System.out.println("Hello, welcome to the JSS program (CLI version) \\o/\n" +
                "If you want to close this program, enter 'quit'\n");
        reader.setOnReaderClosedListener(() -> {
            System.out.println("Exiting menu... Bye!");
            System.exit(0);
        });
        while(reader.isOpen()) display();
    }

    protected abstract void display();
}
