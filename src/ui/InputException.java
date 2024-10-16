package ui;

public class InputException extends RuntimeException {
    public InputException(String message, String input) {
        super("Incorrect input : " + input + "\n" + message);
    }
}
