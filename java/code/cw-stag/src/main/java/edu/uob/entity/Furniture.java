package edu.uob.entity;

import edu.uob.database.EntityData;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;
import edu.uob.util.Assert;

import java.util.HashSet;

public class Furniture extends GameEntity {

    public Furniture(String name, String description) {
        super(name, description);
    }

    @Override
    public void consume(EntityData entityData, Location playerLocation, Player player) throws STAGException {
        HashSet<GameEntity> locationFurnitureSet = playerLocation.getFurnitureSet();
        Assert.isTrue(locationFurnitureSet.contains(this), Response.UNAVAILABLE_ENTITY);
        locationFurnitureSet.remove(this);
        entityData.getStoreroom().getFurnitureSet().add(this);
    }

    @Override
    public void produce(EntityData entityData, Location playerLocation, Player player) throws STAGException {
        // get entity's location, null if it's unavailable
        Location entityLocation = entityData.getEntityLocation(this);
        Assert.notNull(entityLocation, Response.UNAVAILABLE_ENTITY);
        // delete entity from previous location
        entityLocation.getFurnitureSet().remove(this);
        // produce entity at player location
        playerLocation.getFurnitureSet().add(this);
    }
}
