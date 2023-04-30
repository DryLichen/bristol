package edu.uob.cmd;

import edu.uob.action.GameAction;
import edu.uob.database.ActionData;
import edu.uob.database.EntityData;
import edu.uob.entity.*;
import edu.uob.entity.Character;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;

import java.util.HashSet;

public class CmdInterpreter {
    private ActionData actionData;
    private EntityData entityData;

    public CmdInterpreter(ActionData actionData, EntityData entityData) {
        this.actionData = actionData;
        this.entityData = entityData;
    }

    public void interpretCmd(String command) throws STAGException {
        CmdParser cmdParser = new CmdParser(actionData, entityData);
        Cmd cmd = cmdParser.parseCmd(command);

        // get the corresponding gameAction
        GameAction gameAction = cmd.getGameAction();
        // consume game entities
        HashSet<GameEntity> consumeSet = gameAction.getConsumeSet();
        Location playerLocation = entityData.getPlayerLocation(cmd.getPlayer());
        consumeEntity(consumeSet, playerLocation, cmd.getPlayer());

        // produce game entities
        HashSet<GameEntity> produceSet = gameAction.getProduceSet();
        produceEntity(produceSet, playerLocation, cmd.getPlayer());


    }

    /**
     * consume the given gameEntity
     * if the entity is unavailable (not at the location or inventory), throw exception
     */
    private void consumeEntity(HashSet<GameEntity> gameEntitySet, Location playerLocation, Player player) throws STAGException {
        for (GameEntity gameEntity : gameEntitySet) {
            // artefact
            if (gameEntity instanceof Artefact) {
                HashSet<GameEntity> artefactSet = playerLocation.getArtefactSet();
                // delete artefact from location or inventory
                if (artefactSet.contains(gameEntity)) {
                    artefactSet.remove(gameEntity);
                } else if (player.getInventory().contains(gameEntity)) {
                    player.getInventory().remove(gameEntity);
                } else {
                    throw new STAGException(Response.UNAVAILABLE_ENTITY);
                }
                // move artefact to storeroom
                entityData.getStoreroom().getArtefactSet().add(gameEntity);
                continue;
            }

            // furniture
            if (gameEntity instanceof Furniture) {
                HashSet<GameEntity> furnitureSet = playerLocation.getFurnitureSet();
                if (!furnitureSet.contains(gameEntity)) {
                    throw new STAGException(Response.UNAVAILABLE_ENTITY);
                }
                furnitureSet.remove(gameEntity);
                entityData.getStoreroom().getFurnitureSet().add(gameEntity);
                continue;
            }

            // character
            if (gameEntity instanceof Character) {
                HashSet<GameEntity> characterSet = playerLocation.getCharacterSet();
                if (characterSet.contains(gameEntity)) {
                    throw new STAGException(Response.UNAVAILABLE_ENTITY);
                }
                characterSet.remove(gameEntity);
                entityData.getStoreroom().getCharacterSet().add(gameEntity);
            }

            // location
            if (gameEntity instanceof Location) {
                HashSet<String> toLocationSet = playerLocation.getToLocationSet();
                if (!toLocationSet.contains(gameEntity.getName())) {
                    throw new STAGException(Response.UNAVAILABLE_ENTITY);
                }
                toLocationSet.remove(gameEntity.getName());
            }
        }
    }

    /**
     * produce the given entity
     * if produced entity is in other player's inventory or unavailable, throw a exception
     */
    private void produceEntity(HashSet<GameEntity> produceSet, Location playerLocation, Player player) {
        for (GameEntity entity : produceSet) {
            
        }
    }
}
