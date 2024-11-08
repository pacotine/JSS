package ui;

import java.util.Scanner;

/**
 * This exception is a {@link RuntimeException} indicating that a user wanted to stop the interactive {@link CLIMenu}
 * as he typed the keyword {@code quit}.
 */
public class QuitException extends RuntimeException {

    /**
     * Constructs a new {@link QuitException}. Closes the scanner to release the standard input stream.
     * @param scanner the scanner to close.
     */
    public QuitException(Scanner scanner) {
        super("Bye!");
        scanner.close();
    }
}
