package edu.uob.cmd;

import edu.uob.action.GameAction;
import edu.uob.entity.*;

import java.util.ArrayList;

public class Cmd {

    private ArrayList<String> builtInAction = new ArrayList<>();
    private ArrayList<String> actionList = new ArrayList<>();

    private ArrayList<GameEntity> artefactList = new ArrayList<>();
    private ArrayList<GameEntity> furnitureList = new ArrayList<>();
    private ArrayList<GameEntity> locationList = new ArrayList<>();
    private ArrayList<GameEntity> characterList = new ArrayList<>();
    private Player player;

    private GameAction gameAction;

    /**
     * @return all the entities in the command except the player
     */
    public ArrayList<GameEntity> getCmdEntities() {
        ArrayList<GameEntity> cmdEntities = new ArrayList<>();
        cmdEntities.addAll(artefactList);
        cmdEntities.addAll(furnitureList);
        cmdEntities.addAll(characterList);
        cmdEntities.addAll(locationList);

        return cmdEntities;
    }

    public ArrayList<String> getBuiltInAction() {
        return builtInAction;
    }

    public ArrayList<String> getActionList() {
        return actionList;
    }

    public ArrayList<GameEntity> getArtefactList() {
        return artefactList;
    }

    public ArrayList<GameEntity> getFurnitureList() {
        return furnitureList;
    }

    public ArrayList<GameEntity> getLocationList() {
        return locationList;
    }

    public ArrayList<GameEntity> getCharacterList() {
        return characterList;
    }

    public Player getPlayer() {
        return player;
    }

    public GameAction getGameAction() {
        return gameAction;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGameAction(GameAction gameAction) {
        this.gameAction = gameAction;
    }

}
