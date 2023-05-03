package edu.uob.entity;

import edu.uob.database.EntityData;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;
import edu.uob.util.Assert;

import java.util.HashSet;

public class Location extends GameEntity {

    private HashSet<GameEntity> artefactSet = new HashSet<>();
    private HashSet<GameEntity> furnitureSet = new HashSet<>();
    private HashSet<GameEntity> characterSet = new HashSet<>();

    private HashSet<Player> playerSet = new HashSet<>();
    private HashSet<String> toLocationSet = new HashSet<>();

    /**
     * @return all the entities in this location except players
     */
    public HashSet<GameEntity> getAllEntities() {
        HashSet<GameEntity> gameEntities = new HashSet<>();
        gameEntities.addAll(artefactSet);
        gameEntities.addAll(furnitureSet);
        gameEntities.addAll(characterSet);
        return gameEntities;
    }

    @Override
    public void consume(EntityData entityData, Location playerLocation, Player player) throws STAGException {
        HashSet<String> toLocationSet = playerLocation.getToLocationSet();
        Assert.isTrue(toLocationSet.contains(this.getName()), Response.UNAVAILABLE_ENTITY);
        toLocationSet.remove(this.getName());
    }

    @Override
    public void produce(EntityData entityData, Location playerLocation, Player player) throws STAGException {
        HashSet<String> toLocationSet = playerLocation.getToLocationSet();
        toLocationSet.add(this.getName());
    }

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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("You are at location: " + super.toString() + "\n");

        if (artefactSet.size() != 0) {
            stringBuilder.append("There are some artefacts: " + artefactSet + "\n");
        }
        if (furnitureSet.size() != 0) {
            stringBuilder.append("There are some furniture: " + furnitureSet + "\n");
        }
        if (characterSet.size() != 0) {
            stringBuilder.append("There are some character:" + characterSet + "\n");
        }
        if (playerSet.size() != 0) {
            stringBuilder.append("There are some players: " + playerSet + "\n");
        }
        if (toLocationSet.size() != 0) {
            stringBuilder.append("The locations you can go from here: " + toLocationSet + "\n");
        }

        return stringBuilder.toString();
    }
}
