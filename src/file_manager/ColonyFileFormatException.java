package file_manager;

public class ColonyFileFormatException extends IllegalArgumentException {
    public ColonyFileFormatException(String message) {
        super(message);
    }

    static class InvalidArgumentException extends ColonyFileFormatException {
        public InvalidArgumentException(ColonyReader.ColonyFileMethods type, String line, int n) {
            super("Invalid argument at line " + n + " : '" + line + "' is incorrect for " + type.getType() + "()");
        }
    }

    static class InvalidMethodException extends ColonyFileFormatException {
        public InvalidMethodException(String line, int n) {
            super("Unknown method " + line + " at line " + n);
        }
    }
}
