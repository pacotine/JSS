package main.file_manager;

/**
 * Exception class for handling errors related to the format of colony data files.
 * @see ColonyReader
 */
public class ColonyFileFormatException extends IllegalArgumentException {
    /**
     * Constructs a {@link ColonyFileFormatException} with the specified error message.
     *
     * @param message the detail message explaining the error
     */
    public ColonyFileFormatException(String message) {
        super(message);
    }

    /**
     * Constructs a {@link ColonyFileFormatException} with a message containing the line number.
     *
     * @param message the detail message explaining the error
     * @param n       the line number where the error occurred
     */
    public ColonyFileFormatException(String message, int n) {
        this("At line " + n + " : " + message);
    }

    /**
     * Exception class for errors caused by invalid arguments in a specific section of the file.
     */
    static class InvalidArgumentException extends ColonyFileFormatException {
        /**
         * Constructs an {@link InvalidArgumentException} for a specific invalid argument.
         *
         * @param type the section or method where the error occurred
         * @param line the invalid line causing the error
         * @param n    the line number where the error occurred
         */
        public InvalidArgumentException(ColonyReader.ColonyFileMethods type, String line, int n) {
            super("Invalid argument '" + line + "' is incorrect for " + type.getType() + "()", n);
        }
    }

    /**
     * Exception class for errors caused by unrecognized methods in the file.
     */
    static class InvalidMethodException extends ColonyFileFormatException {
        /**
         * Constructs an {@link InvalidMethodException} for an unknown method.
         *
         * @param line the unrecognized method line
         * @param n    the line number where the error occurred
         */
        public InvalidMethodException(String line, int n) {
            super("Unknown method " + line, n);
        }
    }
}
