package file_manager;

public class ColonyFileFormatException extends IllegalArgumentException {
    public ColonyFileFormatException(ColonyReader.ColonyFileMethods type, String line, int n) {
        super("Invalid format at line " + n + " : '" + line + "' is incorrect for " + type);
    }

    public ColonyFileFormatException(ColonyReader.ColonyFileMethods type, String message) {
        super("Incorrect format for type : " + type + "\n" + message);
    }
}
