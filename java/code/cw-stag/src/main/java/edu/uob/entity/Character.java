package edu.uob.entity;

import edu.uob.database.EntityData;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;
import edu.uob.util.Assert;

import java.util.HashSet;

public class Character extends GameEntity {

    public Character(String name, String description) {
        super(name, description);
    }

    @Override
    public void consume(EntityData entityData, Location playerLocation, Player player) throws STAGException {
        HashSet<GameEntity> locationCharaSet = playerLocation.getCharacterSet();
        Assert.isTrue(locationCharaSet.contains(this), Response.UNAVAILABLE_ENTITY);
        locationCharaSet.remove(this);
        entityData.getStoreroom().getFurnitureSet().add(this);
    }

    @Override
    public void produce(EntityData entityData, Location playerLocation, Player player) throws STAGException {
        // get entity's location, null if it's unavailable
        Location entityLocation = entityData.getEntityLocation(this);
        Assert.notNull(entityLocation, Response.UNAVAILABLE_ENTITY);
        // delete entity from previous location
        entityLocation.getCharacterSet().remove(this);
        // produce entity at player location
        playerLocation.getCharacterSet().add(this);
    }
}
