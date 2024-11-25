package file_manager;

import env.Simulation;
import model.Resource;
import model.Settler;

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

public class ColonyReader implements AutoCloseable {
    private Simulation simulation;
    private final Scanner scanner;
    private String lastLine;
    private int lineIndex;

    private static final Pattern ARG_REGEX = Pattern.compile("[a-z]+\\([\\w ]+\\)");
    private static final Pattern ARGS_REGEX = Pattern.compile("[a-z]+\\(([\\w ]+,[\\w ]+)+\\)");
    private static final Pattern MET_REGEX = Pattern.compile("[a-z]+\\(.+\\)");

    public enum ColonyFileMethods {
        SETTLERS("colon"),
        RESOURCES("ressource"),
        PREFERENCES("preferences"),
        BAD_RELATIONS("deteste");

        private final String type;
        ColonyFileMethods(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public static ColonyFileMethods valueOfType(String type) {
            for(ColonyFileMethods m : values()) if(m.getType().equals(type)) return m;
            return null;
        }

        public static boolean next(ColonyFileMethods currentMethod, ColonyFileMethods method) {
            return switch(method) {
                case SETTLERS -> currentMethod == RESOURCES;
                case RESOURCES -> currentMethod == BAD_RELATIONS || currentMethod == PREFERENCES;  //BAD_RELATIONS is optional
                case BAD_RELATIONS -> currentMethod == PREFERENCES;
                case PREFERENCES -> false;
            };
        }
    }

    public ColonyReader(File file) throws IOException {
        this.scanner = new Scanner(file);
        this.lineIndex = 0;
        scanner.useDelimiter("\\.");

        retrieveData();
    }

    private String readLine() {
        if (lastLine != null) {
            String line = lastLine;
            lastLine = null;
            return line;
        }
        lineIndex++;
        return scanner.hasNext() ? scanner.next().trim() : null; //null = EOF
    }

    private void pushBack(String line) {
        this.lastLine = line;
    }

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

    private void readSection(ColonyFileMethods method, Consumer<String> processLine) throws ColonyFileFormatException {
        String line;
        while((line = readLine()) != null) { //while EOF
            //System.out.println("handle line : " + line + " for method " + method);
            ColonyFileMethods currentMethod = method(line);
            //System.out.println(currentMethod + "/" + method + "/" + ColonyFileMethods.next(currentMethod, method));

            //example : blabla(hello) -> blabla doesn't exist
            if(currentMethod == null) throw new ColonyFileFormatException.InvalidMethodException(line, lineIndex);
            //example : we are checking settlers section, but we found a resource method -> this is the end of the settlers section, we should push back this line and handle it in the resource section
            if(ColonyFileMethods.next(currentMethod, method)) { pushBack(line); return; }
            //example : we are in the settlers section, the only method accepted here are 'settlers' or 'resource', if we found i.e. the method for bad relations, this is the wrong place!
            if(currentMethod != method) throw new ColonyFileFormatException(line + " : this method should not be there!", lineIndex);

            //System.out.println(line + " is correct for " + method);
            processLine.accept(line); //example : we are in the settlers section, the method found in this line is 'settler', it's perfect, we can handle it now
        }
    }

    private static ColonyFileMethods method(String line) {
        if(matches(MET_REGEX, line)) return ColonyFileMethods.valueOfType(line.split("\\(")[0]);
        return null;
    }

    private static String arg(String line) {
        if(matches(ARG_REGEX, line)) return line.split("\\(")[1].replace(")", "");
        return null;
    }

    private static String[] args(String line) {
        if(matches(ARGS_REGEX, line)) return line.split("[()]")[1].split(",");
        return null;
    }

    private static boolean matches(Pattern pattern, String line) {
        Matcher m = pattern.matcher(line);
        return m.matches();
    }

    public Simulation initSimulation() throws ColonyFileFormatException {
        if(!simulation.checkIfStable()) throw new ColonyFileFormatException("Simulation is not stable");
        return simulation;
    }

    @Override
    public void close() {
        scanner.close();
    }
}
