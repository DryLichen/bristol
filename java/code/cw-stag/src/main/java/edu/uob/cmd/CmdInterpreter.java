package edu.uob.cmd;

import edu.uob.action.GameAction;
import edu.uob.database.ActionData;
import edu.uob.database.EntityData;
import edu.uob.entity.*;
import edu.uob.entity.Character;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;
import edu.uob.util.Assert;

import java.util.HashSet;

/**
 * interpret command
 */
public class CmdInterpreter {
    private ActionData actionData;
    private EntityData entityData;

    public CmdInterpreter(ActionData actionData, EntityData entityData) {
        this.actionData = actionData;
        this.entityData = entityData;
    }

    /**
     * execute input command
     * @return result of execution
     * @throws STAGException exceptions will be handled by GameServer
     */
    public String interpretCmd(String command) throws STAGException {
        CmdParser cmdParser = new CmdParser(actionData, entityData);
        Cmd cmd = cmdParser.parseCmd(command);

        // get player information
        Player player = cmd.getPlayer();
        Location playerLocation = entityData.getPlayerLocation(player);

        // built-in actions case
        if (cmd.getBuiltInAction().size() != 0) {
            return executeBuiltIn(cmd, playerLocation, player);
        }

        // normal actions case
        GameAction gameAction = cmd.getGameAction();
        return executeNormalAction(gameAction, playerLocation, player);
    }

    /**
     * execute built-in actions
     * @return result of execution
     */
    private String executeBuiltIn(Cmd cmd, Location playerLocation, Player player) throws STAGException {
        String builtIn = cmd.getBuiltInAction().get(0);

        // execute corresponding built-in actions based on trigger
        switch (builtIn.toLowerCase()) {
            case "look":
                return playerLocation.toString();
            case "get":
                return executeGet(cmd, playerLocation, player);
            case "drop":
                return executeDrop(cmd, playerLocation, player);
            case "inv":
            case "inventory":
                return "Items in " + player.getName() + "'s inventory: " + player.getInventory().toString();
            case "health":
                return "Player " + player.getName() + "'s health: " + player.getHealth();
            case "goto":
                return executeGoto(cmd, playerLocation, player);
            default:
                throw new STAGException(Response.LACK_ACTION);
        }
    }

    /**
     * @return result of executing get artefact
     */
    private String executeGet(Cmd cmd, Location playerLocation, Player player) throws STAGException {
        // check if the entity is available
        GameEntity cmdArtefact = cmd.getArtefactList().get(0);
        Assert.isTrue(playerLocation.getArtefactSet().contains(cmdArtefact), Response.UNAVAILABLE_ENTITY);
        // delete entity from current place
        playerLocation.getArtefactSet().remove(cmdArtefact);
        // put entity into player's inventory
        player.getInventory().add((Artefact) cmdArtefact);

        return "get " + cmdArtefact.getName() + " successfully";
    }

    /**
     * @return result of executing drop artefact
     */
    private String executeDrop(Cmd cmd, Location playerLocation, Player player) throws STAGException {
        // check if the entity is available
        GameEntity cmdArtefact = cmd.getArtefactList().get(0);
        Assert.isTrue(player.getInventory().contains(cmdArtefact), Response.UNAVAILABLE_ENTITY);
        // delete entity from player's inventory
        player.getInventory().remove(cmdArtefact);
        // put entity to current location
        playerLocation.getArtefactSet().add(cmdArtefact);

        return "drop " + cmdArtefact.getName() + " successfully";
    }

    /**
     * @return result of executing goto location
     */
    private String executeGoto(Cmd cmd, Location playerLocation, Player player) throws STAGException {
        // check if the location is available
        Location toLocation = (Location) cmd.getLocationList().get(0);
        Assert.isTrue(playerLocation.getToLocationSet().contains(toLocation.getName()), Response.UNAVAILABLE_ENTITY);
        // move player
        playerLocation.getPlayerSet().remove(player);
        toLocation.getPlayerSet().add(player);

        return "Move to " + toLocation.getName() + " successfully";
    }

    /**
     * @return result of the narration of executed action
     */
    private String executeNormalAction(GameAction gameAction, Location playerLocation, Player player) throws STAGException {
        // check if subjects are all available
        HashSet<GameEntity> subjectSet = gameAction.getSubjectSet();
        checkSubjectsAvailable(subjectSet, playerLocation, player);

        // consume health
        consumeHealth(gameAction, playerLocation, player);

        // produce health
        if (gameAction.isProduceHealth()) {
            player.produceHealth();
        }

        // consume game entities
        HashSet<GameEntity> consumeSet = gameAction.getConsumeSet();
        consumeEntities(consumeSet, playerLocation, player);

        // produce game entities
        HashSet<GameEntity> produceSet = gameAction.getProduceSet();
        produceEntities(produceSet, playerLocation, player);

        return gameAction.getNarration();
    }

    /**
     * check if every required subjects in a normal action are available
     * location in toLocations, entities are at current location or in player's inventory
     */
    private void checkSubjectsAvailable(HashSet<GameEntity> subjectSet, Location playerLocation, Player player) throws STAGException {
        for (GameEntity subject : subjectSet) {
            if (playerLocation.getAllEntities().contains(subject)) {
                continue;
            }
            if (playerLocation.getToLocationSet().contains(subject.getName())) {
                continue;
            }
            if (player.getInventory().contains(subject)) {
                continue;
            }
            throw new STAGException(Response.UNAVAILABLE_ENTITY);
        }
    }

    /**
     * consume player's health
     */
    private void consumeHealth(GameAction gameAction, Location playerLocation, Player player) throws STAGException {
        if (gameAction.isConsumeHealth()) {
            boolean isDead = player.consumeHealth();
            // check if player is still alive or dead
            if (isDead) {
                // drop all the entities in inventory to current location
                playerLocation.getArtefactSet().addAll(player.getInventory());
                player.getInventory().clear();
                // move player to spawn point
                playerLocation.getPlayerSet().remove(player);
                entityData.getSpawnPoint().getPlayerSet().add(player);

                // stop following actions
                throw new STAGException(Response.PLAYER_IS_DEAD);
            }
        }
    }

    /**
     * consume the given gameEntity
     * if the entity is unavailable (not at the location or inventory), throw exception
     */
    private void consumeEntities(HashSet<GameEntity> consumeEntitySet, Location playerLocation, Player player) throws STAGException {
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
    private void produceEntities(HashSet<GameEntity> produceSet, Location playerLocation, Player player) throws STAGException {
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
