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

    public void setArtefactSet(HashSet<Artefact> artefactSet) {
        this.artefactSet = artefactSet;
    }

    public void setFurnitureSet(HashSet<Furniture> furnitureSet) {
        this.furnitureSet = furnitureSet;
    }

    public void setPathSet(HashSet<Path> pathSet) {
        this.pathSet = pathSet;
    }

    public void setCharacterSet(HashSet<Character> characterSet) {
        this.characterSet = characterSet;
    }

    public void setPlayerSet(HashSet<Player> playerSet) {
        this.playerSet = playerSet;
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
