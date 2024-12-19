package main.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class CLIReaderTest {

    @Test
    @DisplayName("Close the reader if input is 'quit'")
    public void readerClosesWhenInputIsQuit() {
        try(CLIReader cliReader = new CLIReader(new ByteArrayInputStream("quit".getBytes()))){
            assertTrue(cliReader.isOpen());
            assertEquals("quit", cliReader.readInput());
            assertFalse(cliReader.isOpen());
        }
    }

    @Test
    @DisplayName("Read 3 arguments but give 2")
    public void readArgumentsIsIncorrect() {
        String input = "arg1 arg2";
        try(CLIReader cliReader = new CLIReader(new ByteArrayInputStream(input.getBytes()))){
            assertThrowsExactly(InputException.class, () -> cliReader.readArguments(3));
        }
    }

    @Test
    @DisplayName("Read 3 arguments and give 3")
    public void readArgumentsIsCorrect() {
        String input = "arg1 arg2 arg3";
        try(CLIReader cliReader = new CLIReader(new ByteArrayInputStream(input.getBytes()))){
            assertArrayEquals(new String[]{"arg1", "arg2", "arg3"}, cliReader.readArguments(3));
        }
    }

    @Test
    @DisplayName("Read 3 arguments and give 3 with 1 name-spaced")
    public void readArgumentsWithNameSpacedIsCorrect() {
        String input = "arg1 'arg2 with space' arg3";
        try(CLIReader cliReader = new CLIReader(new ByteArrayInputStream(input.getBytes()))){
            assertArrayEquals(new String[]{"arg1", "arg2 with space", "arg3"}, cliReader.readArguments(3));
        }
    }

    @Test
    @DisplayName("Read integer but give characters")
    public void readIntegerIsIncorrect() {
        String input = "hello";
        try(CLIReader cliReader = new CLIReader(new ByteArrayInputStream(input.getBytes()))){
            assertThrowsExactly(NumberFormatException.class, cliReader::readInteger);
        }
    }

    @Test
    @DisplayName("Read integer and give an integer")
    public void readIntegerIsCorrect() {
        String input = "4";
        try(CLIReader cliReader = new CLIReader(new ByteArrayInputStream(input.getBytes()))){
            assertEquals(4, cliReader.readInteger());
        }
    }
}
