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