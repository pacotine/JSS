package model;

import java.util.*;

/**
 * A <i>settler</i> represents an agent in the assignment problem. It has :
 * <ul>
 * <li> a unique name
 * <li> an ordered list of resources (see {@link env.Simulation})
 * <li> a list of settlers he doesn't like (called <i>bad relations</i>)
 * <li> a unique affectation
 * </ul>
 * Assigning a resource to a settler gives him a level of jealousy
 * based on the resource assignment of settlers he doesn't like.
 */
public class Settler {
    private String name;
    private Set<Settler> badRelations;
    private Resource[] preferences;
    private Resource affectation;
    private int affectationRank;

    /**
     * Constructs an {@link Settler} with the specified name, <i>bad relations</i>, and preferences.
     * @param name the name of the settler
     * @param badRelations a {@link Set} of {@link Settler} containing all the other settlers it doesn't like
     * @param preferences an array of {@link Resource} representing the ordered preferences of this settler
     */
    public Settler(String name, Set<Settler> badRelations, Resource[] preferences) {
        this.name = name;
        this.badRelations = badRelations;
        this.preferences = preferences;
        this.affectationRank = -1;
    }

    /**
     * Constructs an {@link Settler} with the specified name and preferences. By default, this settler doesn't
     * have any <i>bad relations</i>.
     * @param name the name of the settler
     * @param preferences an array of {@link Resource} representing the ordered preferences of this settler
     */
    public Settler(String name, Resource[] preferences) {
        this(name, new HashSet<>(), preferences);
    }

    /**
     * Constructs an {@link Settler} with the specified name.
     * By default, this settler doesn't have any <i>bad relations</i> or preferences.
     * @param name the name of the settler
     */
    public Settler(String name, int n) {
        this(name, new HashSet<>(), new Resource[n]);
    }

    /**
     * Adds the specified {@code settler} to the {@link Set} of bad settler relationships.
     * @param settler the {@link Settler} it detests
     */
    public void addBadRelation(Settler settler) {
        if(settler.equals(this)) throw new IllegalArgumentException("A settler cannot detests his-self");
        badRelations.add(settler);
    }

    /**
     * Retrieves the {@link Resource} affectation of the settler.
     * @return the resource affected to the settler
     */
    public Resource getAffectation() {
        return affectation;
    }

    /**
     * Sets the {@link Resource} affected to the settler.
     * @param affectation the settler affectation
     */
    public void setAffectation(Resource affectation) {
        this.affectation = affectation;
        if(affectation == null) this.affectationRank = -1;
        else this.affectationRank = Arrays.asList(preferences).indexOf(affectation);
    }

    /**
     * Retrieves the name of the settler.
     * @return the name of the settler
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the settler.
     * @param name the new name of the settler
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the {@link Set} of bad settler relationships.
     * @return the set of the settlers this settler hates
     */
    public Set<Settler> getBadRelations() {
        return badRelations;
    }

    /**
     * Sets the {@link Set} of bad settler relationships.
     * @param badRelations the set of the settlers this settler hates
     */
    public void setBadRelations(Set<Settler> badRelations) {
        this.badRelations = badRelations;
    }

    /**
     * Retrieves the {@link Resource} array of this settler's preferences.
     * @return the settler's preferences
     */
    public Resource[] getPreferences() {
        return preferences;
    }

    /**
     * Sets the settler's preferences as an array of {@link Resource} objects.
     * <br>
     * This definition is relative to the settler, but does not necessarily depend on the colony.
     * Call {@link Settler#checkPreferences(int)} to check whether these preferences are valid
     * for a colony with {@code n} settlers.
     * @param preferences the settler's preferences
     */
    public void setPreferences(Resource[] preferences) {
        this.preferences = preferences;
    }

    /**
     * Checks whether the settler's preferences are valid, i.e. whether the settler
     * has {@code n} distinct resources ({@link Resource}).
     * @param n the number of distinct resources the settler should have
     * @return {@code true} if the settler's preferences are valid, {@code false} otherwise
     */
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

    /**
     * Check if the settler is jealous of another (one is enough).
     * @return {@code true} if the settler is jealous, {@code false} otherwise
     */
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
