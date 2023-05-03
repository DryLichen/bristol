package edu.uob.entity;

import edu.uob.database.EntityData;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;
import edu.uob.util.Assert;

import java.util.HashSet;

public class Artefact extends GameEntity {

    public Artefact(String name, String description) {
        super(name, description);
    }

    @Override
    public void consume(EntityData entityData, Location playerLocation, Player player) throws STAGException {
        HashSet<GameEntity> locationArtefactSet = playerLocation.getArtefactSet();
        // delete artefact from location or inventory
        if (locationArtefactSet.contains(this)) {
            locationArtefactSet.remove(this);
        } else if (player.getInventory().contains(this)) {
            player.getInventory().remove(this);
        } else {
            throw new STAGException(Response.UNAVAILABLE_ENTITY);
        }

        // move entity to storeroom
        entityData.getStoreroom().getArtefactSet().add(this);
    }

    @Override
    public void produce(EntityData entityData, Location playerLocation, Player player) throws STAGException {
        // in player's inventory
        if (player.getInventory().contains(this)) {
            player.getInventory().remove(this);
        } else {
            // or in some available locations
            Location entityLocation = entityData.getEntityLocation(this);
            Assert.notNull(entityLocation, Response.UNAVAILABLE_ENTITY);
            // delete entity from previous location
            entityLocation.getArtefactSet().remove(this);
        }

        // produce entity at player location
        playerLocation.getArtefactSet().add(this);
    }
}
