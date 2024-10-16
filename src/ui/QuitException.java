package ui;

import java.util.Scanner;

public class QuitException extends RuntimeException {
    private Scanner scanner;
    public QuitException(Scanner scanner) {
        super("Bye!");
        scanner.close();
    }
}
