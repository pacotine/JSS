package ui;

import java.util.Arrays;
import java.util.Scanner;

public class CLIReader {
    private final Scanner scanner;

    public CLIReader() {
        this.scanner = new Scanner(System.in);
    }

    public String readInput() throws QuitException {
        String res;
        do res = scanner.nextLine();
        while (res.isEmpty());
        if(res.equals("quit")) throw new QuitException(scanner);
        return res;
    }

    public int readInteger() throws NumberFormatException {
        String s = readInput();
        return Integer.parseInt(s);
    }

    public String[] readArguments(int n) throws InputException {
        String[] args = readInput().split(" ");
        if(args.length != n) throw new InputException("Input should have " + n + " arguments (" + args.length + " given)", Arrays.toString(args));
        return args;
    }
}
