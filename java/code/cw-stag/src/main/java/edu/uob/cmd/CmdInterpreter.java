package edu.uob.cmd;

import edu.uob.action.GameAction;
import edu.uob.database.ActionData;
import edu.uob.database.EntityData;
import edu.uob.entity.*;
import edu.uob.entity.Character;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;
import edu.uob.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;

public class CmdInterpreter {
    private ActionData actionData;
    private EntityData entityData;

    public CmdInterpreter(ActionData actionData, EntityData entityData) {
        this.actionData = actionData;
        this.entityData = entityData;
    }

    public String interpretCmd(String command) throws STAGException {
//        StringBuilder result = new StringBuilder();
        String result = null;

        // get cmd and player information
        CmdParser cmdParser = new CmdParser(actionData, entityData);
        Cmd cmd = cmdParser.parseCmd(command);
        Player player = cmd.getPlayer();
        Location playerLocation = entityData.getPlayerLocation(player);

        // built-in action case
        String builtIn = cmd.getBuiltInAction().get(0);
        if (builtIn != null) {
            if ("look".equalsIgnoreCase(builtIn)) {
                return playerLocation.toString();
            }

            if ("get".equalsIgnoreCase(builtIn)) {
                // check if the entity is available
                GameEntity cmdArtefact = cmd.getArtefactList().get(0);
                Assert.isTrue(playerLocation.getArtefactSet().contains(cmdArtefact), Response.UNAVAILABLE_ENTITY);
                // delete entity from this place
                playerLocation.getArtefactSet().remove(cmdArtefact);
                // put entity into player's inventory
                player.getInventory().add((Artefact) cmdArtefact);

                return "get " + cmdArtefact.getName() + " successfully";
            }

            if ("drop".equalsIgnoreCase(builtIn)) {
                // check if the entity is available
                GameEntity cmdArtefact = cmd.getArtefactList().get(0);
                Assert.isTrue(player.getInventory().contains(cmdArtefact), Response.UNAVAILABLE_ENTITY);
                // delete entity from player's inventory
                player.getInventory().remove(cmdArtefact);
                // put entity to current location
                playerLocation.getArtefactSet().add(cmdArtefact);

                return "drop " + cmdArtefact.getName() + " successfully";
            }

            if ("inv".equalsIgnoreCase(builtIn) || "inventory".equalsIgnoreCase(builtIn)) {
                return "Items in " + player.getName() + "'s inventory: " + player.getInventory().toString();
            }

            if ("health".equalsIgnoreCase(builtIn)) {
                return "Player " + player.getName() + "'s health: " + player.getHealth();
            }

            if ("goto".equalsIgnoreCase(builtIn)) {
                // check if the location is available
                Location toLocation = (Location) cmd.getLocationList().get(0);
                Assert.isTrue(playerLocation.getToLocationSet().contains(toLocation.getName()), Response.UNAVAILABLE_ENTITY);
                // move player
                playerLocation.getPlayerSet().remove(player);
                toLocation.getPlayerSet().add(player);

                return "Move to " + toLocation + " successfully";
            }

        }

        // other normal corresponding action case
        GameAction gameAction = cmd.getGameAction();
        // consume game entities
        HashSet<GameEntity> consumeSet = gameAction.getConsumeSet();
        consumeEntity(consumeSet, playerLocation, player);

        // produce game entities
        HashSet<GameEntity> produceSet = gameAction.getProduceSet();
        produceEntity(produceSet, playerLocation, player);

        return gameAction.getNarration();
    }

//    private void map

    /**
     * execute built-in actions
     */
    private void executeBuiltIn() {

    }

    /**
     * consume the given gameEntity
     * if the entity is unavailable (not at the location or inventory), throw exception
     */
    private void consumeEntity(HashSet<GameEntity> consumeEntitySet, Location playerLocation, Player player) throws STAGException {
        for (GameEntity consumeEntity : consumeEntitySet) {
            // artefact
            if (consumeEntity instanceof Artefact) {
                HashSet<GameEntity> artefactSet = playerLocation.getArtefactSet();
                // delete artefact from location or inventory
                if (artefactSet.contains(consumeEntity)) {
                    artefactSet.remove(consumeEntity);
                } else if (player.getInventory().contains(consumeEntity)) {
                    player.getInventory().remove(consumeEntity);
                } else {
                    throw new STAGException(Response.UNAVAILABLE_ENTITY);
                }
                // move artefact to storeroom
                entityData.getStoreroom().getArtefactSet().add(consumeEntity);
                continue;
            }

            // furniture
            if (consumeEntity instanceof Furniture) {
                HashSet<GameEntity> furnitureSet = playerLocation.getFurnitureSet();
                Assert.isTrue(furnitureSet.contains(consumeEntity), Response.UNAVAILABLE_ENTITY);
                furnitureSet.remove(consumeEntity);
                entityData.getStoreroom().getFurnitureSet().add(consumeEntity);
                continue;
            }

            // character
            if (consumeEntity instanceof Character) {
                HashSet<GameEntity> characterSet = playerLocation.getCharacterSet();
                Assert.isTrue(characterSet.contains(consumeEntity), Response.UNAVAILABLE_ENTITY);
                characterSet.remove(consumeEntity);
                entityData.getStoreroom().getCharacterSet().add(consumeEntity);
                continue;
            }

            // location
            if (consumeEntity instanceof Location) {
                HashSet<String> toLocationSet = playerLocation.getToLocationSet();
                Assert.isTrue(toLocationSet.contains(consumeEntity.getName()), Response.UNAVAILABLE_ENTITY);
                toLocationSet.remove(consumeEntity.getName());
            }
        }
    }

    /**
     * produce the given entity
     * if produced entity is in other player's inventory or unavailable, throw a exception
     */
    private void produceEntity(HashSet<GameEntity> produceSet, Location playerLocation, Player player) throws STAGException {
        for (GameEntity produceEntity : produceSet) {
            // artefact
            if (produceEntity instanceof Artefact) {
                // in player's inventory
                if (player.getInventory().contains(produceEntity)) {
                    player.getInventory().remove(produceEntity);
                // or in some available locations
                } else {
                    Location entityLocation = entityData.getEntityLocation(produceEntity);
                    Assert.notNull(entityLocation, Response.UNAVAILABLE_ENTITY);
                    // delete entity from previous location
                    entityLocation.getArtefactSet().remove(produceEntity);
                    // produce entity at player location
                    playerLocation.getArtefactSet().add(produceEntity);
                }
            }

            // furniture
            if (produceEntity instanceof Furniture) {
                Location entityLocation = entityData.getEntityLocation(produceEntity);
                Assert.notNull(entityLocation, Response.UNAVAILABLE_ENTITY);
                // delete entity from previous location
                entityLocation.getFurnitureSet().remove(produceEntity);
                // produce entity at player location
                playerLocation.getFurnitureSet().add(produceEntity);
            }

            // character
            if (produceEntity instanceof Character) {
                Location entityLocation = entityData.getEntityLocation(produceEntity);
                Assert.notNull(entityLocation, Response.UNAVAILABLE_ENTITY);
                // delete entity from previous location
                entityLocation.getCharacterSet().remove(produceEntity);
                // produce entity at player location
                playerLocation.getCharacterSet().add(produceEntity);
            }

            // location
            if (produceEntity instanceof Location) {
                HashSet<String> toLocationSet = playerLocation.getToLocationSet();
                toLocationSet.add(produceEntity.getName());
            }
        }
    }
}
