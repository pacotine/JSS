package ui;

import simulation.Dispatcher;
import simulation.Simulation;

/**
 * This class represents an interactive menu which the user can quit at any time by pressing {@code quit}.
 */
public abstract class CLIMenu {
    protected final CLIReader reader;
    protected Simulation simulation;
    protected static final String INPUT_ONE = "1";
    protected static final String INPUT_TWO = "2";
    protected static final String INPUT_THREE = "3";

    /**
     * Constructs a {@link CLIMenu} by initializing a {@link CLIReader}.
     */
    public CLIMenu() {
        this.reader = new CLIReader();
    }

    /**
     * Sets {@link OnReaderClosedListener} for the reader and starts displaying menu.
     */
    public void start() {
        System.out.println("Hello, welcome to the JSS program (CLI version) \\o/\n" +
                "If you want to close this program, enter 'quit'\n");
        reader.setOnReaderClosedListener(() -> {
            System.out.println("Exiting menu... Bye!");
            System.exit(0);
        });
        while(reader.isOpen()) display();
    }

    /**
     * Prints the dispatcher menu, asking the user whether algorithm he wants to use (linear, MAX-LEF, ...).
     */
    protected void showDispatcherMenu() {
        boolean correct;
        Dispatcher dispatcher = new Dispatcher(simulation);

        do {
            correct = true;
            System.out.println("Please select a dispatcher algorithm : " +
                    "\n\t1. linear" +
                    "\n\t2. MAX-LEF p-approx"
            );
            String res = reader.readInput();

            switch(res) {
                case INPUT_ONE:
                    // assign with linear algorithm
                    System.out.println("Linear dispatch : ");
                    dispatcher.linearDispatch();
                    break;
                case INPUT_TWO:
                    // assign with MAX-LEF p-approx algorithm
                    System.out.println("\n\nMAX-LEF dispatch : ");
                    dispatcher.maxLEFDispatch(simulation.getSettlers().size());
                    break;
                default:
                    correct = false;
                    break;
            }
        } while(!correct);

        simulation.showSettlers();
        simulation.showJealous();
    }

    /**
     * Displays the menu. This method runs as long the {@link CLIReader} is open
     * or until its {@link OnReaderClosedListener} is called back.
     */
    protected abstract void display();
}
