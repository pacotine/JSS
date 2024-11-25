package model;

/**
 * A resource is a unique element of a {@link simulation.Simulation}, which can be assigned to one and only one {@link Settler}.
 */
public class Resource {
    private final String name;
    private boolean isAffected;

    /**
     * Constructs a {@link Resource} with the specified name. By default, this resource is not affected.
     * @param name the name of the resource
     */
    public Resource(String name) {
        if(name.isEmpty()) throw new IllegalArgumentException("Resource name cannot be empty");
        this.name = name;
        this.isAffected = false;
    }

    /**
     * Retrieves the name of the resource.
     * @return the name of the resource
     */
    public String getName() {
        return name;
    }

    /**
     * Indicates whether this resource is affected to a {@link Settler} or not.
     * @return {@code true} if the resource is affected, {@code false} otherwise
     */
    public boolean isAffected() {
        return isAffected;
    }

    /**
     * Sets whether this resource is affected to a {@link Settler} or not.
     * @param affected the affectation status
     */
    public void setAffected(boolean affected) {
        isAffected = affected;
    }

    @Override
    public String toString() {
        return name;
    }
}
