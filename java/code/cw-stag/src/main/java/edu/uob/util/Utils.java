package edu.uob.util;

import edu.uob.database.ActionData;
import edu.uob.database.EntityData;
import edu.uob.entity.Character;
import edu.uob.entity.Furniture;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;

import java.util.HashSet;

public class Utils {
    private EntityData entityData;
    private ActionData actionData;

    public Utils(EntityData entityData, ActionData actionData) {
        this.entityData = entityData;
        this.actionData = actionData;
    }

    public void consumeEntity(GameEntity consumeEntity, Location playerLocation) throws STAGException {
        if (consumeEntity instanceof Furniture) {
            HashSet<GameEntity> furnitureSet = playerLocation.getFurnitureSet();
            Assert.isTrue(furnitureSet.contains(consumeEntity), Response.UNAVAILABLE_ENTITY);
            furnitureSet.remove(consumeEntity);
            entityData.getStoreroom().getFurnitureSet().add(consumeEntity);
        }

        if (consumeEntity instanceof Character) {
            HashSet<GameEntity> characterSet = playerLocation.getCharacterSet();
            Assert.isTrue(characterSet.contains(consumeEntity), Response.UNAVAILABLE_ENTITY);
            characterSet.remove(consumeEntity);
            entityData.getStoreroom().getCharacterSet().add(consumeEntity);
        }
    }
}
