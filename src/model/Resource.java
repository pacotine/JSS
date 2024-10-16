package model;

public class Resource {
    private String name;
    private boolean isAffected;

    public Resource(String name) {
        if(name.isEmpty()) throw new IllegalArgumentException("Resource name cannot be empty");
        this.name = name;
        this.isAffected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAffected() {
        return isAffected;
    }

    public void setAffected(boolean affected) {
        isAffected = affected;
    }

    @Override
    public String toString() {
        return name;
    }
}
