package model;

import java.util.*;

public class Settler {
    private String name;
    private Set<Settler> badRelations;
    private Resource[] preferences;
    private Resource affectation;
    private int affectationRank;

    public Settler(String name, Set<Settler> badRelations, Resource[] preferences) {
        this.name = name;
        this.badRelations = badRelations;
        this.preferences = preferences;
        this.affectationRank = -1;
    }

    public Settler(String name, Resource[] preferences) {
        this(name, new HashSet<>(), preferences);
    }

    public Settler(String name, int n) {
        this(name, new HashSet<>(), new Resource[n]);
    }

    public void addBadRelation(Settler settler) {
        if(settler.equals(this)) throw new IllegalArgumentException("A settler cannot detests his-self");
        badRelations.add(settler);
    }

    public Resource getAffectation() {
        return affectation;
    }

    public void setAffectation(Resource affectation) {
        this.affectation = affectation;
        if(affectation == null) this.affectationRank = -1;
        else this.affectationRank = Arrays.asList(preferences).indexOf(affectation);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Settler> getBadRelations() {
        return badRelations;
    }

    public void setBadRelations(Set<Settler> badRelations) {
        this.badRelations = badRelations;
    }

    public Resource[] getPreferences() {
        return preferences;
    }

    public boolean checkPreferences(int n) {
        try {
            return Set.of(preferences).size() == n;
        } catch(NullPointerException ne) {
            System.out.println("Settler " + name + " : no preferences set");
            return false;
        } catch(IllegalArgumentException ie) {
            System.out.println("Settler " + name + " : invalid order of preferences (duplicates)");
            return false;
        }
    }

    public void setPreferences(Resource[] preferences) {
        this.preferences = preferences;
    }

    public int getAffectationRank() { return affectationRank; }

    public boolean isJealous() {
        if(affectation != null) {
            List<Resource> dreams = Arrays.asList(preferences).subList(0, affectationRank);
            return badRelations.stream().anyMatch(enemy -> dreams.contains(enemy.getAffectation()));
        }
        return false;
    }

    @Override
    public String toString() {
        return name + " | P : " + Arrays.toString(preferences) + " | R : " + affectation + " | J : " + badRelations.stream().map(Settler::getName).toList();
    }
}
