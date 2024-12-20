package main.ui;

import main.simulation.Dispatcher;
import main.simulation.Simulation;

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
        this.reader = new CLIReader(System.in);
    }

    /**
     * Sets {@link OnReaderClosedListener} for the reader and starts displaying menu.
     */
    public void start() {
        System.out.println("""
                Hello, welcome to the JSS program (CLI version) \\o/
                If you want to close this program, enter 'quit' at any time
                """);
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
            System.out.println("""
                    Please select a dispatcher algorithm : \
                    
                    \t1. linear\
                    
                    \t2. MAX-LEF p-approx (k)\
                    
                    \t3. switch (k)
                    """
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
                    System.out.println("""
                            
                            MAX-LEF dispatch :\s""");
                    dispatcher.maxLEFDispatch(simulation.getSettlers().size());
                    break;
                case INPUT_THREE:
                    System.out.println("Switch (brute force) dispatch : ");
                    dispatcher.switchDispatch(simulation.getSettlers().size());
                    break;
                default:
                    System.out.println("Incorrect input : " + res);
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
