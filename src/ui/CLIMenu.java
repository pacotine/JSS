package ui;

import env.Dispatcher;
import env.Simulation;

import java.util.Arrays;

/**
 * This class manages interaction between the user and the simulation of resource allocation to a colony.
 * This is an interactive menu which the user can quit at any time by pressing {@code quit}.
 */
public class CLIMenu {
    private Simulation simulation;
    private CLIReader reader;

    /**
     * Constructs a {@link CLIMenu} by initializing a {@link CLIReader}.
     */
    public CLIMenu() {
        this.reader = new CLIReader();
        System.out.println("Hello, welcome to the JSS program (CLI version) \\o/\n" +
                "If you want to close this program, enter 'quit'\n");
    }

    public CLIMenu(Simulation simulation) {
        this.simulation = simulation;
        this.reader = new CLIReader();
    }

    /**
     * Starts the interactive menu by initializing a simulation and asking the user for modifications.
     * @throws QuitException if the user type, at any moment, the keyword {@code quit}
     */
    public void start() {
        try {
            if(simulation == null) {
                //init();
                initRandom();
            }
            showMainMenu();
            showSubMenu();
        } catch(QuitException ie) {
            System.out.println(ie.getMessage());
        }
    }

    /**
     * Asks the user the number of settlers in the colony before initializing a classic simulation.
     */
    private void init() {
        int n = askN();
        if(n != -1) this.simulation = new Simulation(n);
    }

    /**
     * Initializes a random simulation with {@link Simulation#random(int, int)}.
     */
    private void initRandom() {
        this.simulation = Simulation.random(15, 3);
    }

    /**
     * Uses the {@link CLIReader} to ask the number of settlers for this simulation to the user.
     * @return the number of settlers for this simulation
     * @throws InputException if the number entered by the user is less than {@code 1} or greater than {@code 26}
     * @throws NumberFormatException if the input is not a number
     */
    private int askN() {
        int n;
        boolean correct = false;

        do {
            System.out.println("Please enter how many settlers does your colony have :");
            n = reader.readInteger();

            try {
                if(n < 1 || n > 26) throw new InputException("Your colony should be between 1 and 26 people", String.valueOf(n));
                else correct = true;
            } catch(NumberFormatException ne) {
                System.out.println("Sorry, but this is not a number");
            } catch(InputException ie) {
                System.out.println(ie.getMessage());
            }
        } while(!correct);

        return n;
    }

    /**
     * Prints the main menu, asking the user its customized simulation parameters.
     */
    private void showMainMenu() {
        if(simulation == null) {
            System.out.println("The simulation has not been initialized, please complete the following...");
            init();
        }

        boolean correct = false;
        do {
            simulation.showSettlers();
            System.out.println("Please select an option : " +
                    "\n\t1. add a relation between two settlers" +
                    "\n\t2. add preferences to a settler" +
                    "\n\t3. confirm");
            String res = reader.readInput();

            switch(res) {
                case "1":
                    askRelations();
                    break;
                case "2":
                    askPreferences();
                    break;
                case "3":
                    correct = simulation.checkIfStable();
                    break;
                default:
                    System.out.println("Invalid input (select 1, 2 or 3)");
                    break;
            }
        } while(!correct);

        System.out.println("/!\\\nEverything seems completed! Now, we're gonna find a solution\n");
        showDispatcherMenu();
    }

    /**
     * Prints the submenu, prompting the user his choice for the next step (switch, see jealous, go back to the dispatcher menu).
     */
    private void showSubMenu() {
        System.out.println("Now you can change affectations and see jealous settlers");
        do {
            System.out.println("\nPlease select an option : " +
                    "\n\t1. switch affectations between 2 settlers" +
                    "\n\t2. see all jealous settlers" +
                    "\n\t3. choose another algorithm"
            );
            String res = reader.readInput();

            switch(res) {
                case "1":
                    askSwitch();
                    simulation.showSettlers();
                    break;
                case "2":
                    simulation.showSettlers();
                    simulation.showJealous();
                    break;
                case "3":
                    simulation.clear();
                    showDispatcherMenu();
                    break;
                default:
                    System.out.println("Invalid input (select 1, 2 or 3)");
                    break;
            }
        } while(true);

    }

    /**
     * Prints the dispatcher menu, asking the user whether algorithm he wants to use (linear, MAX-LEF, ...).
     */
    private void showDispatcherMenu() {
        boolean correct;
        // begin affectations
        Dispatcher dispatcher = new Dispatcher(simulation);
        do {
            correct = true;
            System.out.println("Please select a dispatcher algorithm : " +
                    "\n\t1. linear" +
                    "\n\t2. MAX-LEF p-approx"
            );
            String res = reader.readInput();

            switch(res) {
                case "1":
                    // assign with linear algorithm
                    System.out.println("Linear dispatch : ");
                    dispatcher.linearDispatch();
                    break;
                case "2":
                    // assign with MAX-LEF p-approx algorithm
                    System.out.println("\n\nMAX-LEF dispatch : ");
                    dispatcher.maxLEFDispatch(simulation.getSettlers().size());
                    break;
                default:
                    correct = false;
                    break;
            }
        }while(!correct);
        simulation.showSettlers();
        simulation.showJealous();
    }

    /**
     * Prompts the user with the {@link CLIReader}, asking for 2 {@link String} representing 2 settlers' name in order to
     * switch their resources (with {@link Simulation#switchAffectations(String, String)}).
     */
    private void askSwitch() {
        boolean correct = false;
        do {
            System.out.println("Please enter the 2 settlers to switch resources (for example : A1 B1) :");

            try {
                String[] args =  reader.readArguments(2);
                simulation.switchAffectations(args[0], args[1]);
                correct = true;
            } catch(InputException | IllegalArgumentException il) {
                System.out.println(il.getMessage());
            }
        } while(!correct);
    }

    /**
     * Prompts the user with the {@link CLIReader}, asking for 2 {@link String} representing 2 settlers' name in order to
     * create their relation (with {@link Simulation#setBadRelations(String, String)}).
     */
    private void askRelations() {
        boolean correct = false;
        do {
            System.out.println("Please enter the toxic relation between 2 settlers (for example : A1 B1) :");

            try {
                String[] args =  reader.readArguments(2);
                simulation.setBadRelations(args[0], args[1]);
                correct = true;
            } catch(InputException | IllegalArgumentException il) {
                System.out.println(il.getMessage());
            }
        } while(!correct);
    }

    /**
     * Prompts the user with the {@link CLIReader}, asking for a {@link String} representing a settler's name, and {@code n}
     * {@link String} representing its preferences.
     * Then, sets to this settler its preferences using {@link Simulation#setSettlerPreferences(String, String...)}
     */
    private void askPreferences() {
        boolean correct = false;
        do {
            System.out.println("Please enter the preferences of a settler (for example : A1 R1 R3 R2) :");

            try {
                String[] args =  reader.readArguments(simulation.getSettlers().size()+1);
                simulation.setSettlerPreferences(args[0], Arrays.copyOfRange(args, 1, args.length));
                correct = true;
            } catch(InputException | IllegalArgumentException il) {
                System.out.println(il.getMessage());
            }
        } while(!correct);
    }
}
