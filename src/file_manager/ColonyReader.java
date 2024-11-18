package file_manager;

import env.Simulation;
import model.Resource;
import model.Settler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColonyReader implements AutoCloseable {
    private Simulation simulation;
    private final Scanner scanner;

    private static final Pattern ARG_REGEX = Pattern.compile("[a-z]+\\([\\w ]+\\)");
    private static final Pattern ARGS_REGEX = Pattern.compile("[a-z]+\\(([\\w ]+,[\\w ]+)+\\)");
    private static final Pattern MET_REGEX = Pattern.compile("[a-z]+\\(.+\\)");

    public enum ColonyFileMethods {
        SETTLERS("colon"),
        RESOURCES("ressource"),
        PREFERENCES("preferences"),
        BAD_RELATIONS("deteste");

        private String type;
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
    }

    public ColonyReader(File file) throws IOException {
        this.scanner = new Scanner(file);
        scanner.useDelimiter("\\.");

        readFile();
    }

    private String readLine() throws IOException {
        if(scanner.hasNext()) {
            return scanner.next().trim();
        }
        return null; //EOF
    }

    private void readFile() throws IOException {
        retrieveData();
    }

    private void retrieveData() throws IOException, ColonyFileFormatException {
        List<String> settlersNames = new ArrayList<>();
        Map<String, Settler> settlers = new HashMap<>();
        Map<String, Resource> resources = new HashMap<>();
        int i = 1;

        String line = "";
        ColonyFileMethods met = null;

        boolean hasSettler = true;
        while(hasSettler && (line = readLine()) != null) {
            met = method(line);
            if(met == null) throw new ColonyFileFormatException.InvalidMethodException(line, i);
            if(met != ColonyFileMethods.SETTLERS) hasSettler = false;
            else {
                String name = arg(line);
                if(name != null) settlersNames.add(name);
                else throw new ColonyFileFormatException.InvalidArgumentException(ColonyFileMethods.SETTLERS, line, i);
            }
            i++;
        }

        if(settlersNames.isEmpty()) throw new ColonyFileFormatException("No settler defined");
        int colonySize = settlersNames.size();

        for(String name : settlersNames) {
            Settler settler = new Settler(name, colonySize);
            settlers.put(name, settler);
        }

        if(met == ColonyFileMethods.RESOURCES) {
            String firstResourceName = arg(line);
            if(firstResourceName != null) resources.put(firstResourceName, new Resource(firstResourceName));
            else throw new ColonyFileFormatException.InvalidArgumentException(ColonyFileMethods.RESOURCES, line, i-1);
            colonySize--;
        }
        else throw new ColonyFileFormatException("Resources should be set up after settlers");

        while(colonySize > 0 && (line = readLine()) != null && met == ColonyFileMethods.RESOURCES) {
            met = method(line);
            if(met == null) throw new ColonyFileFormatException.InvalidMethodException(line, i);

            String rName = arg(line);
            if(rName != null) {
                Resource resource = new Resource(rName);
                resources.put(rName, resource);
                colonySize--;
            } else throw new ColonyFileFormatException.InvalidArgumentException(ColonyFileMethods.RESOURCES, line, i);

            i++;
        }
        System.out.println(colonySize);
        if(colonySize != 0) throw new ColonyFileFormatException("Invalid number of resources, should be = to settlers number");
        this.simulation = new Simulation(settlers, resources);
        System.out.println("SIMULATION WITH " + settlers.keySet() + "/" + resources.keySet());

        boolean hasBadRelations = true;
        while(hasBadRelations && (line = readLine()) != null) {
            met = method(line);
            if(met == null) throw new ColonyFileFormatException.InvalidMethodException(line, i);
            if(met != ColonyFileMethods.BAD_RELATIONS) hasBadRelations = false;
            else {
                String[] badNames = args(line);
                if (badNames != null && badNames.length == 2) {
                    String b1 = badNames[0];
                    String b2 = badNames[1];
                    simulation.setBadRelations(b1, b2);
                } else throw new ColonyFileFormatException.InvalidArgumentException(ColonyFileMethods.BAD_RELATIONS, line, i);
            }

            i++;
        }

        if(met == ColonyFileMethods.PREFERENCES) {
            String[] firstPreferences = args(line);
            if(firstPreferences != null && firstPreferences.length == resources.size()+1)
                simulation.setSettlerPreferences(firstPreferences[0],
                        Arrays.copyOfRange(firstPreferences, 1, firstPreferences.length));
            else throw new ColonyFileFormatException.InvalidMethodException(line, i-1);
        }
        else throw new ColonyFileFormatException("Excepted "
                + ColonyFileMethods.PREFERENCES.getType()
                + " or " + ColonyFileMethods.BAD_RELATIONS.getType()
                + "/found " + met.getType()
        );
        while((line = readLine()) != null && met == ColonyFileMethods.PREFERENCES) {
            met = method(line);
            if(met == null) throw new ColonyFileFormatException.InvalidMethodException(line, i);

            String[] preferences = args(line);
            if(preferences != null && preferences.length == resources.size()+1) simulation.setSettlerPreferences(preferences[0],
                    Arrays.copyOfRange(preferences, 1, preferences.length));
            else throw new ColonyFileFormatException.InvalidArgumentException(ColonyFileMethods.PREFERENCES, line, i);

            i++;
        }


    }

    private ColonyFileMethods method(String line) {
        if(matches(MET_REGEX, line)) return ColonyFileMethods.valueOfType(line.split("\\(")[0]);
        return null;
    }

    private String arg(String line) {
        if(matches(ARG_REGEX, line)) return line.split("\\(")[1].replace(")", "");
        return null;
    }

    private String[] args(String line) {
        if(matches(ARGS_REGEX, line)) return line.split("[()]")[1].split(",");
        return null;
    }

    private static boolean matches(Pattern pattern, String line) {
        Matcher m = pattern.matcher(line);
        return m.matches();
    }

    public Simulation initSimulation() {
        return simulation;
    }

    @Override
    public void close() {
        scanner.close();
    }
}
