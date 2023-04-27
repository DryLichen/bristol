package edu.uob.entity;

/**
 * store the path from on location to another location
 */
public class Path {
    private Location fromLocation;
    private Location toLocation;

    public Path(Location fromLocation, Location toLocation) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
    }
}
