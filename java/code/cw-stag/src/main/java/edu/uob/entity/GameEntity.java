package edu.uob.entity;

import edu.uob.database.EntityData;
import edu.uob.exception.STAGException;

/**
 * Store data of entities
 */
public abstract class GameEntity {
    private String name;
    private String description;

    public GameEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * consume this gameEntity
     */
    public abstract void consume(EntityData entityData, Location playerLocation, Player player) throws STAGException;

    /**
     * produce this gameEntity
     */
    public abstract void produce(EntityData entityData, Location playerLocation, Player player) throws STAGException;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
