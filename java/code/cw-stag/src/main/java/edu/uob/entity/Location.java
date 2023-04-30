package edu.uob.entity;

import java.util.HashSet;

public class Location extends GameEntity {
    private HashSet<Artefact> artefactSet = new HashSet<>();
    private HashSet<Furniture> furnitureSet = new HashSet<>();
    private HashSet<Character> characterSet = new HashSet<>();
    private HashSet<Player> playerSet = new HashSet<>();
    private HashSet<Path> pathSet = new HashSet<>();

    public Location(String name, String description) {
        super(name, description);
    }

    public HashSet<Artefact> getArtefactSet() {
        return artefactSet;
    }

    public HashSet<Furniture> getFurnitureSet() {
        return furnitureSet;
    }

    public HashSet<Path> getPathSet() {
        return pathSet;
    }

    public HashSet<Character> getCharacterSet() {
        return characterSet;
    }

    public HashSet<Player> getPlayerSet() {
        return playerSet;
    }
}
