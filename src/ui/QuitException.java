package ui;

import java.util.Scanner;

public class QuitException extends RuntimeException {
    public QuitException(Scanner scanner) {
        super("Bye!");
        scanner.close();
    }
}
