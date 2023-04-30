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

    public HashSet<GameEntity> getLocationSet() {
        return locationSet;
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

    /**
     * @return all the game entities except player
     */
    public Set<GameEntity> getAllEntities() {
        HashSet<GameEntity> gameEntities = new HashSet<>();
        gameEntities.addAll(artefactSet);
        gameEntities.addAll(furnitureSet);
        gameEntities.addAll(characterSet);
        gameEntities.addAll(locationSet);

        return gameEntities;
    }
}
