package file_manager;

import env.Simulation;
import model.Resource;
import model.Settler;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ColonyReader implements AutoCloseable {
    private Simulation simulation;
    private final Scanner scanner;

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
            if(met == null) throw new ColonyFileFormatException(ColonyFileMethods.SETTLERS, line, i);
            if(met != ColonyFileMethods.SETTLERS) hasSettler = false;
            else {
                String name = arg(line);
                if(name != null) settlersNames.add(name);
                else throw new ColonyFileFormatException(ColonyFileMethods.SETTLERS, line, i);
            }
            i++;
        }

        if(settlersNames.isEmpty()) throw new ColonyFileFormatException(ColonyFileMethods.SETTLERS, "No settler defined");
        int colonySize = settlersNames.size();

        for(String name : settlersNames) {
            Settler settler = new Settler(name, colonySize);
            settlers.put(name, settler);
        }

        if(met == ColonyFileMethods.RESOURCES) {
            String firstResourceName = arg(line);
            if(firstResourceName != null) resources.put(firstResourceName, new Resource(firstResourceName));
            colonySize--;
        }
        else throw new ColonyFileFormatException(ColonyFileMethods.RESOURCES, "Resources should be set up after settlers");

        while(colonySize > 0 && (line = readLine()) != null && met == ColonyFileMethods.RESOURCES) {
            met = method(line);
            if(met == null) throw new ColonyFileFormatException(ColonyFileMethods.RESOURCES, line, i);

            String rName = arg(line);
            if(rName != null) {
                Resource resource = new Resource(rName);
                resources.put(rName, resource);
                colonySize--;
            } else throw new ColonyFileFormatException(ColonyFileMethods.RESOURCES, line, i);

            i++;
        }
        System.out.println(colonySize);
        if(colonySize != 0) throw new ColonyFileFormatException(ColonyFileMethods.RESOURCES,
                "Invalid number of resources, should be = to settlers number");
        this.simulation = new Simulation(settlers, resources);
        System.out.println("SIMULATION WITH " + settlers.keySet() + "/" + resources.keySet());

        boolean hasBadRelations = true;
        while(hasBadRelations && (line = readLine()) != null) {
            met = method(line);
            if(met == null) throw new ColonyFileFormatException(ColonyFileMethods.BAD_RELATIONS, line, i);
            if(met != ColonyFileMethods.BAD_RELATIONS) hasBadRelations = false;
            else {
                String[] badNames = args(line);
                if (badNames != null) {
                    String b1 = badNames[0];
                    String b2 = badNames[1];
                    simulation.setBadRelations(b1, b2);
                } else throw new ColonyFileFormatException(ColonyFileMethods.BAD_RELATIONS, line, i);
            }

            i++;
        }

        if(met == ColonyFileMethods.PREFERENCES) {
            String[] firstPreferences = args(line);
            if(firstPreferences != null)
                simulation.setSettlerPreferences(firstPreferences[0],
                        Arrays.copyOfRange(firstPreferences, 1, firstPreferences.length));
        }
        else throw new ColonyFileFormatException(ColonyFileMethods.PREFERENCES, "Preferences should be set up at the end");
        while((line = readLine()) != null && met == ColonyFileMethods.PREFERENCES) {
            met = method(line);
            if(met == null) throw new ColonyFileFormatException(ColonyFileMethods.PREFERENCES, line, i);

            String[] preferences = args(line);
            if(preferences != null) simulation.setSettlerPreferences(preferences[0],
                    Arrays.copyOfRange(preferences, 1, preferences.length));
            else throw new ColonyFileFormatException(ColonyFileMethods.RESOURCES, line, i);

            i++;
        }


    }

    private ColonyFileMethods method(String line) {
        if(line.matches("[a-z]+\\(.+\\)")) return ColonyFileMethods.valueOfType(line.split("\\(")[0]);
        return null;
    }

    private String arg(String line) {
        if(line.matches("[a-z]+\\([\\w ]+\\)")) return line.split("\\(")[1].replace(")", "");
        return null;
    }

    private String[] args(String line) {
        if(line.matches("[a-z]+\\(([\\w ]+,[\\w ]+)+\\)")) return line.split("[()]")[1].split(",");
        return null;
    }

    public Simulation initSimulation() {
        return simulation;
    }

    @Override
    public void close() {
        scanner.close();
    }
}
