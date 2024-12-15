package main.file_manager;

import main.simulation.Simulation;
import main.model.Resource;
import main.model.Settler;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for reading and parsing colony data files, initializing
 * a simulation with settlers, resources, relationships, and preferences.
 * Implements {@link AutoCloseable} to manage file resources.
 */
public class ColonyReader implements AutoCloseable {
    private Simulation simulation;
    private final Scanner scanner;
    private String lastLine;
    private int lineIndex;

    private static final Pattern ARG_REGEX = Pattern.compile("[a-z]+\\([\\w ]+\\)");
    private static final Pattern ARGS_REGEX = Pattern.compile("[a-z]+\\(([\\w ]+,[\\w ]+)+\\)");
    private static final Pattern MET_REGEX = Pattern.compile("[a-z]+\\(.+\\)");

    /**
     * Enum representing the various sections or methods expected in the colony file.
     */
    public enum ColonyFileMethods {
        /**
         * Represents the settlers section.
         */
        SETTLERS("colon"),
        /**
         * Represents the resources section.
         */
        RESOURCES("ressource"),
        /**
         * Represents the preferences section.
         */
        PREFERENCES("preferences"),
        /**
         * Represents the bad relations section.
         */
        BAD_RELATIONS("deteste");

        private final String type;
        ColonyFileMethods(String type) {
            this.type = type;
        }

        /**
         * Returns the string identifier for the method.
         *
         * @return the string identifier.
         */
        public String getType() {
            return type;
        }

        /**
         * Finds the corresponding {@link ColonyFileMethods} for a given type string.
         *
         * @param type the type string
         * @return the matching {@code ColonyFileMethods}, or {@code null} if none match
         */
        public static ColonyFileMethods valueOfType(String type) {
            for(ColonyFileMethods m : values()) if(m.getType().equals(type)) return m;
            return null;
        }

        /**
         * Determines whether a section can validly follow another section.
         *
         * @param currentMethod the current section
         * @param method        the next section
         * @return {@code true} if the next section is valid, {@code false} otherwise
         */
        public static boolean next(ColonyFileMethods currentMethod, ColonyFileMethods method) {
            return switch(method) {
                case SETTLERS -> currentMethod == RESOURCES;
                case RESOURCES -> currentMethod == BAD_RELATIONS || currentMethod == PREFERENCES;  //BAD_RELATIONS is optional
                case BAD_RELATIONS -> currentMethod == PREFERENCES;
                case PREFERENCES -> false;
            };
        }
    }

    /**
     * Constructs a {@link ColonyReader} for the given file and starts parsing it.
     *
     * @param file the input file to read
     * @throws IOException if the file cannot be read
     */
    public ColonyReader(File file) throws IOException {
        this.scanner = new Scanner(file);
        this.lineIndex = 0;
        scanner.useDelimiter("\\.");

        retrieveData();
    }

    /**
     * Reads the next line from the file, or returns {@code null} if end of file is reached.
     *
     * @return the next trimmed line or {@code null}
     */
    private String readLine() {
        if (lastLine != null) {
            String line = lastLine;
            lastLine = null;
            return line;
        }
        lineIndex++;
        return scanner.hasNext() ? scanner.next().trim() : null;
    }

    /**
     * Pushes back a line to be reprocessed on the next read.
     *
     * @param line the line to push back
     */
    private void pushBack(String line) {
        this.lastLine = line;
    }

    /**
     * Parses the colony file to retrieve simulation data, ensuring validity of all sections.
     *
     * @throws ColonyFileFormatException if the file format is invalid
     */
    private void retrieveData() throws ColonyFileFormatException {
        Set<String> settlersNames = new HashSet<>();
        Map<String, Settler> settlers = new HashMap<>();
        Map<String, Resource> resources = new HashMap<>();

        //checking SETTLERS section first
        readSection(ColonyFileMethods.SETTLERS, line -> {
            String name = arg(line);
            if (name != null) settlersNames.add(name);
            else throw new ColonyFileFormatException.InvalidArgumentException(ColonyFileMethods.SETTLERS, line, lineIndex);
        });

        //this section should not be empty, at least one settler has to be set
        if (settlersNames.isEmpty()) {
            throw new ColonyFileFormatException("Settlers should be defined first");
        }
        settlersNames.forEach(name -> settlers.put(name, new Settler(name, settlersNames.size()))); //put settlers into the map

        //then checking RESOURCES section
        readSection(ColonyFileMethods.RESOURCES, line -> {
            String resourceName = arg(line);
            if (resourceName != null) resources.put(resourceName, new Resource(resourceName));
            else throw new ColonyFileFormatException.InvalidArgumentException(ColonyFileMethods.RESOURCES, line, lineIndex);
        });

        //number of lines in the file should be the same
        if (resources.size() != settlersNames.size()) {
            throw new ColonyFileFormatException("Number of resources must equal number of settlers, " +
                    "but there are " + settlersNames.size() + " distinct names for " + resources.size() + " distinct resources");
        }

        this.simulation = new Simulation(settlers, resources); //create simulation

        //checking optional BAD_RELATIONS section
        readSection(ColonyFileMethods.BAD_RELATIONS, line -> {
            String[] badRelation = args(line);
            if (badRelation != null && badRelation.length == 2) {
                simulation.setBadRelations(badRelation[0], badRelation[1]);
            } else throw new ColonyFileFormatException.InvalidArgumentException(ColonyFileMethods.BAD_RELATIONS, line, lineIndex);

        });

        //checking the last section
        readSection(ColonyFileMethods.PREFERENCES, line -> {
            String[] preferences = args(line);
            if(preferences == null) throw new ColonyFileFormatException.InvalidArgumentException(ColonyFileMethods.PREFERENCES, line, lineIndex);
            int size = resources.size()+1, args = preferences.length;
            if(args != size) throw new ColonyFileFormatException(args > size ?
                    "Extra " + (args-size) + " argument(s) for " + line
                    : "Missing " + (size-args) + " argument(s) for " + line, lineIndex);

            simulation.setSettlerPreferences(preferences[0], Arrays.copyOfRange(preferences, 1, preferences.length));
        });
    }

    /**
     * Reads and processes a specific section of the file.
     *
     * @param method      the expected section method
     * @param processLine a consumer function to handle each line in the section
     * @throws ColonyFileFormatException if the section is invalid or improperly formatted
     */
    private void readSection(ColonyFileMethods method, Consumer<String> processLine) throws ColonyFileFormatException {
        String line;
        while((line = readLine()) != null) {
            if(line.isEmpty()) continue;
            ColonyFileMethods currentMethod = method(line);
            //example : blabla(hello) -> blabla doesn't exist
            if(currentMethod == null) throw new ColonyFileFormatException.InvalidMethodException(line, lineIndex);
            //example : we are checking settlers section, but we found a resource method -> this is the end of the settlers section, we should push back this line and handle it in the resource section
            if(ColonyFileMethods.next(currentMethod, method)) { pushBack(line); return; }
            //example : we are in the settlers section, the only method accepted here are 'settlers' or 'resource', if we found i.e. the method for bad relations, this is the wrong place!
            if(currentMethod != method) throw new ColonyFileFormatException(line + " : this method should not be there!", lineIndex);
            //example : we are in the settlers section, the method found in this line is 'settler', it's perfect, we can handle it now
            processLine.accept(line);
        }
    }

    /**
     * Determines the method type of given line.
     *
     * @param line the line to analyze
     * @return the detected {@link ColonyFileMethods}, or {@code null} if none match
     */
    private static ColonyFileMethods method(String line) {
        if(matches(MET_REGEX, line)) return ColonyFileMethods.valueOfType(line.split("\\(")[0]);
        return null;
    }

    /**
     * Extracts a single argument from a line.
     *
     * @param line the line to analyze
     * @return the argument string, or {@code null} if the line format is invalid
     */
    private static String arg(String line) {
        if(matches(ARG_REGEX, line)) return line.split("\\(")[1].replace(")", "");
        return null;
    }

    /**
     * Extracts multiple arguments from a line.
     *
     * @param line the line to analyze
     * @return an array of arguments, or {@code null} if the line format is invalid
     */
    private static String[] args(String line) {
        if(matches(ARGS_REGEX, line)) return line.split("[()]")[1].split(",");
        return null;
    }

    /**
     * Checks whether a line matches a specific pattern.
     *
     * @param pattern the pattern to check against
     * @param line    the line to test
     * @return {@code true} if the line matches the pattern, {@code false} otherwise
     */
    private static boolean matches(Pattern pattern, String line) {
        Matcher m = pattern.matcher(line);
        return m.matches();
    }

    /**
     * Initializes the simulation with parsed data and validates its stability.
     *
     * @return the initialized {@link Simulation}
     * @throws ColonyFileFormatException if the simulation is unstable or invalid
     */
    public Simulation initSimulation() throws ColonyFileFormatException {
        if(!simulation.checkIfStable()) throw new ColonyFileFormatException("Simulation is not stable");
        return simulation;
    }

    /**
     * Closes the scanner to release file resources.
     */
    @Override
    public void close() {
        scanner.close();
    }
}
