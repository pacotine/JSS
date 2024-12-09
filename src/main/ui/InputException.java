package main.ui;

/**
 * This exception is a {@link RuntimeException} indicating incorrect input during a stream read by a {@link CLIReader}.
 */
public class InputException extends RuntimeException {

    /**
     * Constructs a new {@link InputException}.
     * @param message the message for this exception
     * @param input the incorrect input
     */
    public InputException(String message, String input) {
        super("Incorrect input : " + input + "\n" + message);
    }
}
