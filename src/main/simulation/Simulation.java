package main.simulation;

import main.model.Resource;
import main.model.Settler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class represents the main.simulation of a colony where each
 * settler fights for the best available resource against its enemy in the colony.
 * <br>
 * A main.simulation is the set of a <a href="https://en.wikipedia.org/wiki/Resource_allocation">resource allocation problem</a>.
 * Given a set of agents N = {1, ..., n} and a set of indivisible resources O = {o1, ..., or},
 * the number of resources is equal to the number of agents, i.e., r = n, and each settler
 * i ∈ N should receive exactly one resource o ∈ O.
 * <br>
 * In this context, our goal is to minimize the number of envy settlers. The definition of <i>envy</i> is when
 * a settler prefers the share of some other settlers over her own.
 * <br><br>
 * There are various methods and algorithms for finding a result.
 * Here, it's a {@link Dispatcher} that will allocate (dispatch) resources to settlers according to the method used.
 */
public class Simulation {
    private final Map<String, Resource> resources;
    private final Map<String, Settler> settlers;

    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /**
     * Constructs a new {@link Simulation} with the specified number of settlers {@code n}.
     * @param n the number of settlers (and so resources) in the colony
     */
    public Simulation(int n) {
        this.resources = new HashMap<>();
        this.settlers = new HashMap<>();
        init(n);
    }

    public Simulation(Map<String, Settler> settlers, Map<String, Resource> resources) {
        this.resources = resources;
        this.settlers = settlers;
    }

    /**
     * Constructs a new {@link Simulation} with the specified number of settlers {@code n} by random states :
     * each of the {@code n} settlers will have a maximum specified number {@code d} of enemy (<i>bad relations</i>) in the colony,
     * and their preferences will be random (by shuffling the resources set of this main.simulation).
     * @param n the number of settlers (and so resources) in the colony
     * @param d the density of <i>bad relations</i> (each settler will have a random set of enemy between {@code 0} and {@code d})
     * @return the random main.simulation instance
     */
    public static Simulation random(int n, int d) {
        //d is density of the graph
        assert d < n;

        Simulation simulation = new Simulation(n);
        Random r = new Random();
        List<Resource> resourcesList = Arrays.asList(simulation.resources.values().toArray(Resource[]::new));
        List<String> settlersKeys = new ArrayList<>(simulation.getSettlersMap().keySet());
        for(int i = 0; i < n; i++) {
            Collections.shuffle(settlersKeys);
            int rand = r.nextInt(d);
            String siKey = settlersKeys.get(i);
            for(String badKey : settlersKeys.subList(0, rand)) {
                if(!siKey.equals(badKey)) simulation.setBadRelations(siKey, badKey);
            }

            Collections.shuffle(resourcesList);
            simulation.getSettlers().get(i).setPreferences(resourcesList.toArray(Resource[]::new));
        }

        return simulation;
    }

    /**
     * Initializes the (classic) main.simulation.
     * Each settler will be named by a letter of the alphabet + a number if duplicates.
     * Each resource will be named by {@code R} + a unique number.
     * @param n the number of settlers in the colony
     */
    private void init(int n) {
        for(int i = 1; i <= n; i++) {
            String sn = String.valueOf(ALPHABET[(i-1)%26])+((i-1)/26+1);
            resources.put("R" + i, new Resource("R"+i));
            settlers.put(sn, new Settler(sn, n));
        }
        System.out.println("Settlers : " + settlers.keySet());
    }

    /**
     * Prints all the settlers in the colony.
     */
    public void showSettlers() {
        for(Settler s : settlers.values()) {
            System.out.println(s);
        }
    }

    /**
     * Prints all the jealous settlers in the colony and the number of jealous.
     */
    public void showJealous() {
        int sum = 0;
        for(Settler s : settlers.values()) {
            if(s.isJealous()) {
                sum++;
                System.out.println(s.getName() + " is jealous");
            }
        }
        System.out.println("\nThere are " + sum + " jealous settlers");
    }

    /**
     * Retrieves the number of jealous settlers in the colony.
     * @return the number of jealous settlers
     */
    public int getJealousNumber() {
        int sum = 0;
        for(Settler s : settlers.values()) {
            if(s.isJealous()) {
                sum++;
            }
        }
        return sum;
    }

    /**
     * Switches the resource affected to the settler {@code sn1} with the resource affected to the settler {@code sn2}.
     * @param sn1 the first settler in this exchange
     * @param sn2 the second settler in this exchange
     * @throws IllegalArgumentException <ul>
     * <li>if {@code sn1} or {@code sn2} don't have any resource affected
     * <li>if {@code sn1} or {@code sn2} are not part in this colony
     * </ul>
     */
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

    /**
     * Affects the specified resource to the specified settler. If the resource is already affected, this call has no effect.
     * @param settlerName the name of the settler that will receive the resource
     * @param resourceName the name of the resource to affect
     */
    public void affect(String settlerName, String resourceName) {
        Resource resource = resources.get(resourceName);

        if (resource.isAffected()) {
            System.out.println(resourceName + " not affected to '" + settlerName + "' as '" + resourceName + "' is already affected");
        } else {
            settlers.get(settlerName).setAffectation(resource);
            resource.setAffected(true);
        }
    }

    /**
     * Makes the two specified settlers in the colony enemies.
     * @param sn1 the first settler in this <i>bad relation</i>
     * @param sn2 the second settler in this <i>bad relation</i>
     */
    public void setBadRelations(String sn1, String sn2) {
        Settler s1 = settlers.get(sn1);
        Settler s2 = settlers.get(sn2);

        if (s1 == null) throw new IllegalArgumentException("Settler '" + sn1 + "' does not exist");
        if (s2 == null) throw new IllegalArgumentException("Settler '" + sn2 + "' does not exist");

        s1.addBadRelation(s2);
        s2.addBadRelation(s1);
    }

    /**
     * Sets the preferences list for the specified settler in the colony.
     * @param settlerName the name of the settler to set its preferences
     * @param preferencesNames the resource names <b>ordered</b> by the settler's preferences
     * @throws IllegalArgumentException if
     */
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

        s.setPreferences(preferences);
    }

    /**
     * Checks whether this main.simulation is <i>stable</i> or not. A main.simulation is stable if each of the {@code n} settlers
     * has an ordered set of preferences of size {@code n}.
     * @return {@code true} if the main.simulation is stable, {@code false} otherwise
     */
    public boolean checkIfStable() {
        boolean correct = true;
        int n = resources.size();
        for(Settler settler : settlers.values()) {
            if(!settler.checkPreferences(n)) correct = false;
        }
        return correct;
    }

    /**
     * Clears the main.simulation. That means, clear the affectation for all settlers.
     */
    public void clear() {
        for(Settler s : settlers.values()) {
            s.setAffectation(null);
        }
        for(Resource r : resources.values()) {
            r.setAffected(false);
        }
    }

    /**
     * Sets an affection to all settlers, given a {@link Map} where the keys are {@link String} representing a settler's
     * name, and the values are {@link Resource} objects representing its affectation.
     * @param affectations a {@link Map} representing the tuples (settler's name, affectation)
     */
    public void setAffectations(Map<String, Resource> affectations) {
        for(String s : affectations.keySet()) {
            settlers.get(s).setAffectation(affectations.get(s));
        }
    }

    /**
     * Retrieves the colony, i.e, returns the {@link List} of settlers in the main.simulation.
     * @return the list of settlers in the main.simulation
     */
    public List<Settler> getSettlers() {
        return settlers.values().stream().toList();
    }

    /**
     * Retrieves the {@link Map} of tuples (settler's name, settler object) for this main.simulation.
     * @return the {@link Map} of tuples (settler's name, settler object) for this main.simulation.
     */
    public Map<String, Settler> getSettlersMap() {
        return settlers;
    }
}
