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

public class CmdTokenizer {
    private ActionData actionData;
    private EntityData entityData;

    public CmdTokenizer(ActionData actionData, EntityData entityData) {
        this.actionData = actionData;
        this.entityData = entityData;
    }

    /**
     * @return tokens put in Cmd instance
     */
    public Cmd tokenizeCommand(String command) throws STAGException {
        Cmd cmd = new Cmd();
        // process command to convert letters to lowercase
        command = command.trim();
        command = command.toLowerCase();

        // get player
        String player = getPlayer(command);
        cmd.setPlayer(new Player(player, null));

        // get tokens
        String[] tokens = getTokens(command.substring(command.indexOf(":") + 1));

        // classify tokens and store tokens into Cmd instance
        outer:
        for (String token : tokens) {
            // check if the token is action trigger
            // built-in action
            for (String builtIn : actionData.getBuiltIn()) {
                if (token.equalsIgnoreCase(builtIn)) {
                    cmd.getActionList().add(token);
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
                    cmd.getArtefactList().add((Artefact) gameEntity);
                    continue outer;
                }
            }

            // check if the token is furniture
            for (GameEntity gameEntity : entityData.getFurnitureSet()) {
                if (token.equalsIgnoreCase(gameEntity.getName())) {
                    cmd.getFurnitureList().add((Furniture) gameEntity);
                    continue outer;
                }
            }

            // check if the token is character
            for (GameEntity gameEntity : entityData.getCharacterSet()) {
                if (token.equalsIgnoreCase(gameEntity.getName())) {
                    cmd.getCharacterList().add((Character) gameEntity);
                    continue outer;
                }
            }

            // check if the token is location
            for (GameEntity gameEntity : entityData.getLocationSet()) {
                if (token.equalsIgnoreCase(gameEntity.getName())) {
                    cmd.getLocationList().add((Location) gameEntity);
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

    /**
     * @return literal tokens by splitting the command
     */
    private String[] getTokens(String command) {
        // get all the keywords to be identified in commands
        HashSet<String> keywords = new HashSet<>();
        keywords.addAll(actionData.getActionMap().keySet());
        keywords.addAll(entityData.getAllEntities().
                stream().map(e -> e.getName()).collect(Collectors.toSet()));

        // add @ around keywords to separate them with decorative words
        for (String keyword : keywords) {
            command.replace(keyword.toLowerCase(), "@" + keyword + "@");
        }
        // remove all double @
        while (command.contains("@@")) {
            command.replace("@@", "@");
        }
        // split command in terms of @
        String[] tokens = command.split("@");

        return tokens;
    }

}
