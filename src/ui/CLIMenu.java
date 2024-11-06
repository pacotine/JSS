package ui;

import env.Dispatcher;
import env.Simulation;

import java.util.Arrays;

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
            //init();
            initRandom();
            //initDev();
            showMainMenu();
            showSubMenu();
        } catch(QuitException ie) {
            System.out.println(ie.getMessage());
        }
    }

    private void init() {
        int n = askN();
        if(n != -1) this.simulation = new Simulation(n);
    }

    private void initDev() {
        this.simulation = new Simulation(5);
        String[] r = new String[5];
        for(int i = 0; i < 5; i++) {
            r[i] = "R"+(i+1);
        }

        simulation.setSettlerPreferences("A1", r[1], r[2], r[3], r[4], r[0]);
        simulation.setSettlerPreferences("B1", r[2], r[1], r[3], r[0], r[4]);
        simulation.setSettlerPreferences("C1", r[0], r[2], r[4], r[3], r[1]);
        simulation.setSettlerPreferences("D1", r[3], r[0], r[1], r[4], r[2]);
        simulation.setSettlerPreferences("E1", r[0], r[4], r[3], r[1], r[2]);

        simulation.setBadRelations("A1", "B1");
        simulation.setBadRelations("B1", "C1");
        simulation.setBadRelations("E1", "C1");
        simulation.setBadRelations("D1", "C1");
    }

    private void initRandom() {
        this.simulation = Simulation.random(15, 3);
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

        System.out.println("/!\\\nEverything seems completed! Now, we're gonna find a solution\n");
        Dispatcher dispatcher = new Dispatcher(simulation);
        System.out.println("Linear dispatch : ");
        dispatcher.linearDispatch();
        simulation.showJealous();
        System.out.println("\n\nMAX-LEF dispatch : ");
        dispatcher.maxLEFDispatch();
        simulation.showJealous();
        simulation.showSettlers();
    }

    private void showSubMenu() {
        System.out.println("Now you can change affectations and see jealous settlers");
        do {
            System.out.println("\nPlease select an option : " +
                    "\n\t1. switch affectations between 2 settlers" +
                    "\n\t2. see all jealous settlers");
            String res = reader.readInput();

            switch(res) {
                case "1":
                    askSwitch();
                    simulation.showSettlers();
                    break;
                case "2":
                    simulation.showJealous();
                    break;
                default:
                    System.out.println("Invalid input (select 1 or 2)");
                    break;
            }
        } while(true);

    }

    private void askSwitch() {
        boolean correct = false;
        do {
            System.out.println("Please enter the 2 settlers to switch resources (in example : A B) :");

            try {
                String[] args =  reader.readArguments(2);
                simulation.switchAffectations(args[0], args[1]);
                correct = true;
            } catch(InputException | IllegalArgumentException il) {
                System.out.println(il.getMessage());
            }
        } while(!correct);
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
