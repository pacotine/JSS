package ui;

import env.Simulation;

import java.util.Arrays;
import java.util.Scanner;

public class CLIMenu {
    private Simulation simulation;
    private CLIReader reader;

    public CLIMenu() {
        this.reader = new CLIReader();
        System.out.println("Hello, welcome to the JSS program (CLI version) \\o/\n" +
                "If you want to close this program, enter 'quit'\n");
    }

    public void start() {
        try {
            init();
            showMainMenu();
        } catch(QuitException ie) {
            System.out.println(ie.getMessage());
        }
    }

    private void init() {
        int n = askN();
        if(n != -1) this.simulation = new Simulation(n);
    }

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

        System.out.println("Everything seems completed! Now, we're gonna find a solution");

    }

    private void askRelations() {
        boolean correct = false;
        do {
            System.out.println("Please enter the toxic relation between 2 settlers (in example : A B) :");

            try {
                String[] args =  reader.readArguments(2);
                simulation.setBadRelations(args[0], args[1]);
                correct = true;
            } catch(InputException | IllegalArgumentException il) {
                System.out.println(il.getMessage());
            }
        } while(!correct);
    }

    private void askPreferences() {
        boolean correct = false;
        do {
            System.out.println("Please enter the preferences of a settler (in example : A R1 R3 R2) :");

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
