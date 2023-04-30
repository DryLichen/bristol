package edu.uob.cmd;

import edu.uob.entity.*;
import edu.uob.entity.Character;

import java.util.ArrayList;

public class Cmd {
    // there could be duplicated actions or entities in a command
    private ArrayList<String> actionList = new ArrayList<>();
    private ArrayList<Artefact> artefactList = new ArrayList<>();
    private ArrayList<Furniture> furnitureList = new ArrayList<>();
    private ArrayList<Location> locationList = new ArrayList<>();
    private ArrayList<Character> characterList = new ArrayList<>();
    // only one player per command
    private Player player;

    public void execute() {

    }

    public ArrayList<String> getActionList() {
        return actionList;
    }

    public ArrayList<Artefact> getArtefactList() {
        return artefactList;
    }

    public ArrayList<Furniture> getFurnitureList() {
        return furnitureList;
    }

    public ArrayList<Location> getLocationList() {
        return locationList;
    }

    public ArrayList<Character> getCharacterList() {
        return characterList;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
