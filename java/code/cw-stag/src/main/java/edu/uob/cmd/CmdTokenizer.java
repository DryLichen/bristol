package edu.uob.cmd;

import edu.uob.database.ActionData;
import edu.uob.database.EntityData;
import edu.uob.entity.*;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;
import edu.uob.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

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
        // process command to convert letters to lowercase
        command = command.trim().toLowerCase();

        // get command player
        String playerName = getPlayer(command);
        cmd.setPlayer((Player) entityData.getPlayerByName(playerName));

        // get command tokens
        String[] tokens = getTokens(command.substring(command.indexOf(":") + 1));

        // check if there are extraneous health token
        int countHealth = 0;
        for (String token : tokens) {
            if ("health".equalsIgnoreCase(token)) {
                countHealth++;
            }
        }
        Assert.isTrue(countHealth <= 1, Response.TOO_MANY_ACTION);

        // classify tokens and store tokens into Cmd instance
        outer:
        for (String token : tokens) {
            // check if the token is built-in action
            for (String builtIn : actionData.getBuiltInAction()) {
                if (token.equalsIgnoreCase(builtIn)) {
                    cmd.getBuiltInAction().add(token);
                    continue outer;
                }
            }

            // normal action
            for (String trigger : actionData.getActionMap().keySet()) {
                if (token.equalsIgnoreCase(trigger)) {
                    cmd.getActionList().add(token);
                    continue outer;
                }
            }

            // check if the token is artefact
            for (GameEntity gameEntity : entityData.getArtefactSet()) {
                if (token.equalsIgnoreCase(gameEntity.getName())) {
                    cmd.getArtefactList().add(gameEntity);
                    continue outer;
                }
            }

            // check if the token is furniture
            for (GameEntity gameEntity : entityData.getFurnitureSet()) {
                if (token.equalsIgnoreCase(gameEntity.getName())) {
                    cmd.getFurnitureList().add(gameEntity);
                    continue outer;
                }
            }

            // check if the token is character
            for (GameEntity gameEntity : entityData.getCharacterSet()) {
                if (token.equalsIgnoreCase(gameEntity.getName())) {
                    cmd.getCharacterList().add(gameEntity);
                    continue outer;
                }
            }

            // check if the token is location
            for (GameEntity gameEntity : entityData.getLocationSet()) {
                if (token.equalsIgnoreCase(gameEntity.getName())) {
                    cmd.getLocationList().add(gameEntity);
                    continue outer;
                }
            }
        }

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

    private void setCmdPlayer() {

    }

    /**
     * @return literal tokens by splitting the command
     */
    private String[] getTokens(String command) {
        // make sure there is only one space at the beginning and end of the command
        command = " " + command.trim() + " ";

        // get all the keywords to be identified in commands
        HashSet<String> keywords = new HashSet<>();
        keywords.addAll(actionData.getActionMap().keySet());
        keywords.addAll(actionData.getBuiltInAction());
        keywords.addAll(entityData.getAllEntities().
                stream().map(e -> e.getName()).collect(Collectors.toSet()));

        // add @ around keywords to separate them with decorative words
        for (String keyword : keywords) {
            command = command.replace(" " + keyword.toLowerCase() + " ", " @" + keyword + "@ ");
        }
        while (command.contains("@@")) {
            command.replace("@@", "@");
        }
        // split command in terms of @
        String[] tokens = command.split("@");

        return tokens;
    }

}
