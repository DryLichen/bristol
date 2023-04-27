package edu.uob.database;

import edu.uob.entity.*;
import edu.uob.entity.Character;

import java.util.HashSet;

public class EntityData {
    private HashSet<Location> locationSet = new HashSet<>();
    private HashSet<Artefact> artefactSet = new HashSet<>();
    private HashSet<Furniture> furnitureSet = new HashSet<>();
    private HashSet<Character> characterSet = new HashSet<>();
    private HashSet<Player> playerSet = new HashSet<>();

    public void setLocationSet(HashSet<Location> locationSet) {
        this.locationSet = locationSet;
    }

    public void setArtefactSet(HashSet<Artefact> artefactSet) {
        this.artefactSet = artefactSet;
    }

    public void setFurnitureSet(HashSet<Furniture> furnitureSet) {
        this.furnitureSet = furnitureSet;
    }

    public void setCharacterSet(HashSet<Character> characterSet) {
        this.characterSet = characterSet;
    }

    public void setPlayerSet(HashSet<Player> playerSet) {
        this.playerSet = playerSet;
    }

    public HashSet<Location> getLocationSet() {
        return locationSet;
    }

    public HashSet<Artefact> getArtefactSet() {
        return artefactSet;
    }

    public HashSet<Furniture> getFurnitureSet() {
        return furnitureSet;
    }

    public HashSet<Character> getCharacterSet() {
        return characterSet;
    }

    public HashSet<Player> getPlayerSet() {
        return playerSet;
    }
}
