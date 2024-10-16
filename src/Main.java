import env.Simulation;
import ui.CLIMenu;

public class Main {
    public static void main(String[] args) {
        CLIMenu cliMenu = new CLIMenu();
        cliMenu.start();
    }

    /**
     * Simulation simulation = new Simulation(3);
     *         simulation.setBadRelations("A", "B");
     *         simulation.setBadRelations("A", "C");
     *         simulation.setSettlerPreferences("A", "R1", "R2", "R3");
     *         simulation.setSettlerPreferences("B", "R1", "R2", "R3");
     *         simulation.setSettlerPreferences("C", "R3", "R1", "R2");
     *         simulation.affect("A", "R3");
     *         simulation.affect("B", "R3"); //no
     *         simulation.showSettlers();
     */
}