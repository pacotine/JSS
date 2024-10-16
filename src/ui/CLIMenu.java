package ui;

import env.Simulation;

import java.util.Arrays;
import java.util.Scanner;

public class CLIMenu {
    private Simulation simulation;

    public CLIMenu() {
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

    private static int askN() {
        int n;
        Scanner scanner = new Scanner(System.in);
        String res;
        boolean correct = false;

        do {
            n = -1;
            System.out.println("Please enter how many settlers does your colony have :");
            res = scanner.next();
            if(res.equals("quit")) {
                throw new QuitException(scanner);
            }

            try {
                n = Integer.parseInt(res);
                if(n < 1 || n > 26) throw new InputException("Your colony should be between 1 and 26 people", res);
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

        Scanner scanner = new Scanner(System.in);
        String res;
        boolean correct = false;
        do {
            simulation.showSettlers();
            System.out.println("Please select an option : " +
                    "\n\t1. add a relation between two settlers" +
                    "\n\t2. add preferences to a settler" +
                    "\n\t3. confirm");
            res = scanner.next();

            if(res.equals("quit")) {
                throw new QuitException(scanner);
            }

            switch(res) {
                case "1":
                    askRelations(scanner);
                    break;
                case "2":
                    break;
                case "3":
                    //check
                    correct = true;
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        } while(!correct);

        //compute...

    }

    private void askRelations(Scanner scanner) {
        boolean correct = false;
        String res = "";
        do {
            System.out.println("Please enter the toxic relation between 2 settlers (in example : A B) :");

            do res = scanner.nextLine();
            while (res.isEmpty());

            try {
                String[] sep = res.split(" ");
                if(sep.length != 2) throw new InputException("Input should have 2 names (like 'A B')", Arrays.toString(sep));
                simulation.setBadRelations(sep[0], sep[1]);
                correct = true;
            } catch(InputException | IllegalArgumentException il) {
                System.out.println(il.getMessage());
            }
        } while(!correct);
    }
}
