package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a utility class for standard input using a {@link Scanner}.
 */
public class CLIReader {
    private final Scanner scanner;

    /**
     * Constructs a new {@link CLIReader} by initializing a new {@link Scanner} for the standard input stream.
     */
    public CLIReader() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the next line.
     * @return the next line as a {@link String}
     * @throws QuitException if the user type, at any moment, the keyword {@code quit}
     */
    public String readInput() throws QuitException {
        String res;
        do res = scanner.nextLine();
        while (res.isEmpty());
        if(res.equals("quit")) throw new QuitException(scanner);
        return res;
    }

    /**
     * Reads the next line and parses the result as an integer.
     * @return an integer read from the line
     * @throws NumberFormatException if the input is not an integer
     */
    public int readInteger() throws NumberFormatException {
        String s = readInput();
        return Integer.parseInt(s);
    }

    /**
     * Reads the next line and returns an array of {@code n} arguments (separated words by a space).
     * Handles arguments enclosed in single quotes.
     * @param n the number of arguments to read
     * @return an array of {@code n} arguments
     * @throws InputException if the input doesn't contain exactly {@code n} arguments
     */
    public String[] readArguments(int n) throws InputException {
        String input = readInput();

        Matcher matcher = Pattern.compile("'([^']*)'(?!\\S)|(\\S+)").matcher(input);
        List<String> args = new ArrayList<>();

        String gr;
        while (matcher.find()) {
            if((gr = matcher.group(1)) != null) { //group with single quotes
                args.add(gr);
            } else if((gr = matcher.group(2)) != null) { //group with simple space
                args.add(gr);
            }
        }

        if (args.size() != n) throw new InputException(
                    "Input should have " + n + " arguments (" + args.size() + " given)",
                    args.toString()
        );

        return args.toArray(new String[0]);
    }
}
