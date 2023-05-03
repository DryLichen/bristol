package edu.uob.cmd;

import edu.uob.database.ActionData;
import edu.uob.database.EntityData;
import edu.uob.entity.*;
import edu.uob.entity.Character;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;
import edu.uob.util.Assert;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Tokenize command string and save tokens into cmd
 */
public class CmdTokenizer {
    private ActionData actionData;
    private EntityData entityData;

    public CmdTokenizer(ActionData actionData, EntityData entityData) {
        this.actionData = actionData;
        this.entityData = entityData;
    }

    /**
     * @return Cmd instance with tokens in it
     */
    public Cmd tokenizeCommand(String command) throws STAGException {
        Cmd cmd = new Cmd();
        command = command.trim().toLowerCase();

        // get cmd player
        cmd.setPlayer((Player) entityData.getPlayerByName(getPlayer(command)));

        // get command tokens
        String[] tokens = getTokens(command.substring(command.indexOf(":") + 1));

        // check if there are extraneous health tokens
        checkHealthTokens(tokens);

        // classify tokens and store them in cmd
        storeCmdTokens(cmd, tokens);

        return cmd;
    }

    /**
     * @return name of the player
     */
    private String getPlayer(String command) throws STAGException {
        // check if the position of colon is valid
        int indexOfColon = command.indexOf(":");
        Assert.isTrue(indexOfColon != -1, Response.WRONG_PLAYER_NAME);
        Assert.isTrue(indexOfColon != command.length() - 1, Response.LACK_COMMAND);

        String player = command.substring(0, indexOfColon);
        Assert.isTrue(!player.isEmpty(), Response.WRONG_PLAYER_NAME);
        Assert.isTrue(checkPlayerName(player), Response.WRONG_PLAYER_NAME);

        return player;
    }

    /**
     * @return true if player name is valid, else return false
     */
    private boolean checkPlayerName(String playerName) {
        for (int i = 0; i < playerName.length(); i++) {
            if (java.lang.Character.isAlphabetic(playerName.charAt(i))) {
                continue;
            }
            if (java.lang.Character.isWhitespace(playerName.charAt(i))) {
                continue;
            }
            if (playerName.charAt(i) == '\'') {
                continue;
            }
            if (playerName.charAt(i) == '-') {
                continue;
            }
            return false;
        }

        return true;
    }

    /**
     * @return literal tokens by splitting the command
     */
    private String[] getTokens(String command) {
        // get all the keywords to be identified in commands
        HashSet<String> keywords = new HashSet<>();
        keywords.addAll(actionData.getActionMap().keySet());
        keywords.addAll(actionData.getBuiltInAction());
        keywords.addAll(entityData.getAllEntities().
                stream().map(e -> e.getName()).collect(Collectors.toSet()));

        // add @ around keywords to separate them with decorative words
        for (String keyword : keywords) {
            String keywordLow = keyword.toLowerCase();
            command = command.replaceAll("(?<=^|[\\p{P}\\s])(" + keywordLow + ")(?=[\\p{P}\\s]|$)", "@$1@");
        }
        // split command in terms of @
        String[] tokens = command.split("@");

        return tokens;
    }

    /**
     * check if there are more than 1 "health" tokens
     */
    private void checkHealthTokens(String[] tokens) throws STAGException {
        int countHealth = 0;
        for (String token : tokens) {
            if ("health".equalsIgnoreCase(token)) {
                countHealth++;
            }
        }
        Assert.isTrue(countHealth <= 1, Response.TOO_MANY_ACTION);
    }

    /**
     * classify tokens by gameEntities and store them in cmd
     */
    private void storeCmdTokens(Cmd cmd, String[] tokens) {
        for (String token : tokens) {
            // check if the token is built-in action
            if (storeBuiltIn(token, cmd)) {
                continue;
            }
            // check if the token is normal action
            if (storeNormalAction(cmd, token)) {
                continue;
            }

            // store gameEntities in cmd
            GameEntity gameEntity = entityData.getEntityByName(token);
            if (gameEntity == null) {
                continue;
            }
            if (gameEntity instanceof Artefact) {
                cmd.getArtefactList().add(gameEntity);
            } else if (gameEntity instanceof Furniture) {
                cmd.getFurnitureList().add(gameEntity);
            } else if (gameEntity instanceof Character) {
                cmd.getCharacterList().add(gameEntity);
            } else if (gameEntity instanceof Location) {
                cmd.getLocationList().add(gameEntity);
            }
        }
    }

    /**
     * @return true if successfully store the token as built-in action
     */
    private boolean storeBuiltIn(String token, Cmd cmd) {
        for (String builtIn : actionData.getBuiltInAction()) {
            if (token.equalsIgnoreCase(builtIn)) {
                cmd.getBuiltInAction().add(builtIn);
                return true;
            }
        }

        return false;
    }

    /**
     * @return true if successfully store the token as normal action
     */
    private boolean storeNormalAction(Cmd cmd, String token) {
        for (String trigger : actionData.getActionMap().keySet()) {
            if (token.equalsIgnoreCase(trigger)) {
                cmd.getActionList().add(trigger);
                return true;
            }
        }
        return false;
    }
}
