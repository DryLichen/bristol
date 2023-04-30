package edu.uob.entity;

import java.util.HashSet;

public class Location extends GameEntity {

    private HashSet<GameEntity> artefactSet = new HashSet<>();
    private HashSet<GameEntity> furnitureSet = new HashSet<>();
    private HashSet<GameEntity> characterSet = new HashSet<>();

    private HashSet<Player> playerSet = new HashSet<>();
    private HashSet<String> toLocationSet = new HashSet<>();

    public Location(String name, String description) {
        super(name, description);
    }

    public HashSet<GameEntity> getArtefactSet() {
        return artefactSet;
    }

    public HashSet<GameEntity> getFurnitureSet() {
        return furnitureSet;
    }

    public HashSet<GameEntity> getCharacterSet() {
        return characterSet;
    }

    public HashSet<Player> getPlayerSet() {
        return playerSet;
    }

    public HashSet<String> getToLocationSet() {
        return toLocationSet;
    }
}
