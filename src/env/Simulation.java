package env;

import model.Resource;
import model.Settler;

import java.util.*;

public class Simulation {
    private Map<String, Resource> resources;
    private Map<String, Settler> settlers;

    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public Simulation(int n) {
        this.resources = new HashMap<>();
        this.settlers = new HashMap<>();
        init(n);
    }

    public static Simulation random(int n) {
        Simulation simulation = new Simulation(n);
        Random r = new Random();
        List<Resource> resourcesList = Arrays.asList(simulation.resources.values().toArray(Resource[]::new));
        for(int i = 0; i < n; i++) {
            String s1;
            String s2;
            do {
                s1 = String.valueOf(ALPHABET[1+r.nextInt(n-1)]);
                s2 = String.valueOf(ALPHABET[1+r.nextInt(n-1)]);
            } while(s1.equals(s2));

            simulation.setBadRelations(s1, s2);
            Collections.shuffle(resourcesList);
            simulation.getSettlers().get(i).setPreferences(resourcesList.toArray(Resource[]::new));
        }

        return simulation;
    }

    private void init(int n) {
        for(int i = 1; i <= n; i++) {
            String sn = String.valueOf(ALPHABET[i-1]);
            resources.put("R" + i, new Resource("R"+i));
            settlers.put(sn, new Settler(sn, n));
        }
    }

    public void showSettlers() {
        for(Settler s : settlers.values()) {
            System.out.println(s);
        }
    }

    public void showJealous() {
        for(Settler s : settlers.values()) {
            if(s.isJealous()) System.out.println(s.getName() + " is jealous");
        }
    }

    public void switchAffectations(String sn1, String sn2) {
        Settler s1 = settlers.get(sn1);
        Settler s2 = settlers.get(sn2);

        if (s1 == null) throw new IllegalArgumentException("Settler '" + sn1 + "' does not exist");
        if (s2 == null) throw new IllegalArgumentException("Settler '" + sn2 + "' does not exist");

        Resource rs1 = s1.getAffectation();
        Resource rs2 = s2.getAffectation();

        if (rs1 == null) throw new IllegalArgumentException("Settler '" + sn1 + "' does not have any resource");
        if (rs2 == null) throw new IllegalArgumentException("Settler '" + sn2 + "' does not have any resource");

        s1.setAffectation(rs2);
        s2.setAffectation(rs1);
    }

    public void affect(String settlerName, String resourceName) {
        Resource resource = resources.get(resourceName);

        if (resource.isAffected()) {
            System.out.println(resourceName + " not affected to '" + settlerName + "' as '" + resourceName + "' is already affected");
        } else {
            settlers.get(settlerName).setAffectation(resource);
            resource.setAffected(true);
        }
    }

    public void setBadRelations(String sn1, String sn2) {
        Settler s1 = settlers.get(sn1);
        Settler s2 = settlers.get(sn2);

        if (s1 == null) throw new IllegalArgumentException("Settler '" + sn1 + "' does not exist");
        if (s2 == null) throw new IllegalArgumentException("Settler '" + sn2 + "' does not exist");

        s1.addBadRelation(s2);
        s2.addBadRelation(s1);
    }

    public void setSettlerPreferences(String settlerName, String... preferencesNames) {
        if(preferencesNames.length != resources.size()) throw new IllegalArgumentException("Missing " + (resources.size()-preferencesNames.length) + " resource(s)");

        Settler s = settlers.get(settlerName);
        if(s == null) throw new IllegalArgumentException("Settler '" + settlerName + "' does not exist");

        //O(n) because a settler have an ordered array of n resources = preferences
        Resource[] preferences = Arrays.stream(preferencesNames)
                .map(resourceName -> {
                    Resource resource = resources.get(resourceName);
                    if (resource == null) throw new IllegalArgumentException("Resource '" + resourceName + "' does not exist.");
                    return resource;
                })
                .toArray(Resource[]::new);
        //Resource[] preferences = new Resource[preferencesNames.length];
        //for(int i = 0; i < preferences.length; i++) {
          //  preferences[i] = resources.get(preferencesNames[i]);
        //}

        s.setPreferences(preferences);
    }

    public boolean checkIfStable() {
        boolean correct = true;
        int n = resources.size();
        for(Settler settler : settlers.values()) {
            if(!settler.checkPreferences(n)) correct = false;
        }
        return correct;
    }

    public Map<String, Resource> getResources() {
        return resources;
    }

    public void setResources(Map<String, Resource> resources) {
        this.resources = resources;
    }

    public List<Settler> getSettlers() {
        return settlers.values().stream().toList();
    }

    public Map<String, Settler> getSettlersMap() {
        return settlers;
    }

    public void setSettlersMap(Map<String, Settler> settlers) {
        this.settlers = settlers;
    }
}
