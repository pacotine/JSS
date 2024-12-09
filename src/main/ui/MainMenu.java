package main.ui;

import main.simulation.Simulation;

import java.util.Arrays;

/**
 * This class manages interaction between the user and the main.simulation of resource allocation to a colony, manually defined.
 */
public class MainMenu extends CLIMenu {

    public MainMenu() {}

    /**
     * Displays the main menu.
     * @see CLIMenu#display()
     */
    @Override
    protected void display() {
        if(simulation == null) {
            init();
            //initRandom();
        }
        simulation.showSettlers();

        System.out.println("""
                Please select an option :\
                
                \t1. add a relation between two settlers\
                
                \t2. add preferences to a settler\
                
                \t3. confirm""");
        String res = reader.readInput();

        switch(res) {
            case INPUT_ONE:
                askRelations();
                break;
            case INPUT_TWO:
                askPreferences();
                break;
            case INPUT_THREE:
                if(simulation.checkIfStable()) {
                    System.out.println("""
                            /!\\\
                            
                            Everything seems completed! Now, we're gonna find a solution
                            """);
                    showDispatcherMenu();
                    showSubMenu();
                }
                break;
            default:
                System.out.println("Invalid input (select 1, 2 or 3)");
                break;
        }

    }

    /**
     * Asks the user the number of settlers in the colony before initializing a classic main.simulation.
     */
    private void init() {
        int n = askN();
        if(n != -1) this.simulation = new Simulation(n);
    }

    /**
     * Initializes a random main.simulation with {@link Simulation#random(int, int)}.
     */
    private void initRandom() {
        this.simulation = Simulation.random(15, 3);
    }

    /**
     * Uses the {@link CLIReader} to ask the number of settlers for this main.simulation to the user.
     * @return the number of settlers for this main.simulation
     * @throws InputException if the number entered by the user is less than {@code 1} or greater than {@code 26}
     * @throws NumberFormatException if the input is not a number
     */
    private int askN() {
        int n = 0;
        boolean correct = false;

        do {
            System.out.println("Please enter how many settlers does your colony have :");
            try {
                n = reader.readInteger();
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
     * Prints the submenu, prompting the user his choice for the next step (switch, see jealous, go back to the dispatcher menu).
     */
    private void showSubMenu() {
        System.out.println("Now you can change affectations and see jealous settlers");
        do {
            System.out.println("""
                    
                    Please select an option :\
                    
                    \t1. switch affectations between 2 settlers\
                    
                    \t2. see all jealous settlers\
                    
                    \t3. choose another algorithm"""
            );
            String res = reader.readInput();

            switch(res) {
                case INPUT_ONE:
                    askSwitch();
                    simulation.showSettlers();
                    break;
                case INPUT_TWO:
                    simulation.showSettlers();
                    simulation.showJealous();
                    break;
                case INPUT_THREE:
                    simulation.clear();
                    showDispatcherMenu();
                    break;
                default:
                    System.out.println("Invalid input (select 1, 2 or 3)");
                    break;
            }
        } while(reader.isOpen());
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
