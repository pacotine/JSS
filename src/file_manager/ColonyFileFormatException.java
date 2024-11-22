package file_manager;

public class ColonyFileFormatException extends IllegalArgumentException {
    public ColonyFileFormatException(String message) {
        super(message);
    }

    public ColonyFileFormatException(String message, int n) {
        this("At line " + n + " : " + message);
    }

    static class InvalidArgumentException extends ColonyFileFormatException {
        public InvalidArgumentException(ColonyReader.ColonyFileMethods type, String line, int n) {
            super("Invalid argument '" + line + "' is incorrect for " + type.getType() + "()", n);
        }
    }

    static class InvalidMethodException extends ColonyFileFormatException {
        public InvalidMethodException(String line, int n) {
            super("Unknown method " + line, n);
        }
    }
}
