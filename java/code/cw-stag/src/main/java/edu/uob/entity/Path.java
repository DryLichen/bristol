package edu.uob.entity;

/**
 * store the path from on location to another location
 */
public class Path {
    private String fromLocation;
    private String toLocation;

    public Path(String fromLocation, String toLocation) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
    }
}
