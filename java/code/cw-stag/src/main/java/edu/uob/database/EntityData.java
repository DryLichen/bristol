package edu.uob.database;

import edu.uob.entity.*;

import java.util.HashSet;
import java.util.Set;

/**
 * store all the entities data
 */
public class EntityData {
    private HashSet<GameEntity> locationSet = new HashSet<>();
    private Location spawnPoint;
    private Location storeroom;
    private HashSet<GameEntity> artefactSet = new HashSet<>();
    private HashSet<GameEntity> furnitureSet = new HashSet<>();
    private HashSet<GameEntity> characterSet = new HashSet<>();
    private HashSet<GameEntity> playerSet = new HashSet<>();

    /**
     * @return all the game entities except players
     */
    public Set<GameEntity> getAllEntities() {
        HashSet<GameEntity> gameEntities = new HashSet<>();
        gameEntities.addAll(artefactSet);
        gameEntities.addAll(furnitureSet);
        gameEntities.addAll(characterSet);
        gameEntities.addAll(locationSet);

        return gameEntities;
    }

    /**
     * @return gameEntity with the given name, return null if can't found one
     */
    public GameEntity getEntityByName(String name) {
        for (GameEntity entity : getAllEntities()) {
            if (entity.getName().equalsIgnoreCase(name)) {
                return entity;
            }
        }
        return null;
    }

    /**
     * @return player with given name
     * if it doesn't exist, create a new player and return it
     */
    public GameEntity getPlayerByName(String playName) {
        for (GameEntity player : playerSet) {
            if (player.getName().equalsIgnoreCase(playName)) {
                return player;
            }
        }

        // create a new player
        Player player = new Player(playName, null);
        playerSet.add(player);
        // add player to spawn point
        spawnPoint.getPlayerSet().add(player);
        return player;
    }

    /**
     * @return location of the given player, return null if can't find one
     */
    public Location getPlayerLocation(Player player) {
        for (GameEntity entity : locationSet) {
            Location location = (Location) entity;
            if (location.getPlayerSet().contains(player)) {
                return location;
            }
        }

        return null;
    }

    /**
     * @return location of the given entity, return null if can't find one
     */
    public Location getEntityLocation(GameEntity gameEntity) {
        for (GameEntity entity : locationSet) {
            Location location = (Location) entity;
            if (location.getAllEntities().contains(gameEntity)) {
                return location;
            }
        }

        return null;
    }

    public HashSet<GameEntity> getLocationSet() {
        return locationSet;
    }

    public Location getStoreroom() {
        return storeroom;
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

    public HashSet<GameEntity> getPlayerSet() {
        return playerSet;
    }

    public void setSpawnPoint(Location spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public void setStoreroom(Location storeroom) {
        this.storeroom = storeroom;
    }

}
