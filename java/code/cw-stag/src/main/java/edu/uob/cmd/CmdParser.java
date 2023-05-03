package edu.uob.cmd;

import edu.uob.action.GameAction;
import edu.uob.database.ActionData;
import edu.uob.database.EntityData;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;
import edu.uob.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class CmdParser {
    private ActionData actionData;
    private EntityData entityData;

    public CmdParser(ActionData actionData, EntityData entityData) {
        this.actionData = actionData;
        this.entityData = entityData;
    }

    public Cmd parseCmd(String command) throws STAGException {
        CmdTokenizer cmdTokenizer = new CmdTokenizer(actionData, entityData);
        Cmd cmd = cmdTokenizer.tokenizeCommand(command);

        // check if the syntax of cmd is valid
        ArrayList<String> cmdBuiltIn = cmd.getBuiltInAction();
        ArrayList<String> cmdActions = cmd.getActionList();
        Assert.isTrue(cmdBuiltIn.size() <= 1, Response.TOO_MANY_ACTION);
        // cmd contains built-in action
        if (cmdBuiltIn.size() == 1) {
            parseBuiltIn(cmdActions, cmdBuiltIn, cmd);
        }

        // cmd contains normal actions
        if (cmdBuiltIn.size() == 0) {
            parseAction(cmdActions, cmd);
        }

        return cmd;
    }

    private void parseBuiltIn(ArrayList<String> cmdActions, ArrayList<String> cmdBuiltIn, Cmd cmd) throws STAGException {
        Assert.isTrue(cmdActions.size() == 0, Response.TOO_MANY_ACTION);
        String builtIn = cmdBuiltIn.get(0);
        // built-in command can't contain furniture and character
        Assert.isTrue(cmd.getFurnitureList().size() == 0, Response.TOO_MANY_ENTITY);
        Assert.isTrue(cmd.getCharacterList().size() == 0, Response.TOO_MANY_ENTITY);

        if ("goto".equalsIgnoreCase(builtIn)) {
            Assert.isTrue(cmd.getArtefactList().size() == 0, Response.TOO_MANY_ENTITY);
            Assert.isTrue(cmd.getLocationList().size() == 1, Response.ONE_LOCATION);
            return;
        }

        if ("look".equalsIgnoreCase(builtIn) || "inv".equalsIgnoreCase(builtIn) ||
                "inventory".equalsIgnoreCase(builtIn) || "health".equalsIgnoreCase(builtIn)) {
            Assert.isTrue(cmd.getArtefactList().size() == 0, Response.TOO_MANY_ENTITY);
            Assert.isTrue(cmd.getLocationList().size() == 0, Response.TOO_MANY_ENTITY);
            return;
        }

        if ("drop".equalsIgnoreCase(builtIn) || "get".equalsIgnoreCase(builtIn)) {
            Assert.isTrue(cmd.getArtefactList().size() == 1, Response.LACK_ENTITY);
            Assert.isTrue(cmd.getLocationList().size() == 0, Response.UNAVAILABLE_ENTITY);
        }
    }

    private void parseAction(ArrayList<String> cmdActions, Cmd cmd) throws STAGException {
        Assert.isTrue(cmdActions.size() != 0, Response.LACK_ACTION);
        Assert.isTrue(cmd.getCmdEntities().size() != 0, Response.LACK_ENTITY);

        // get all possible actions mapped by triggers
        HashSet<GameAction> intersection = new HashSet<>();
        for (int i = 0; i < cmdActions.size(); i++) {
            // initial intersection set
            if (i == 0) {
                intersection.addAll(actionData.getActionMap().get(cmdActions.get(0)));
            }
            intersection.retainAll(actionData.getActionMap().get(cmdActions.get(i)));
        }

        // check extraneous entities
        // delete action from action set if the action doesn't contain all the cmd entities
        Iterator<GameAction> iterator = intersection.iterator();
        while (iterator.hasNext()) {
            GameAction gameAction = iterator.next();
            boolean isValid = gameAction.getSubjectSet().containsAll(cmd.getCmdEntities());
            if (!isValid) {
                iterator.remove();
            }
        }
        // not ambiguous: check if there are only one action left
        Assert.isTrue(intersection.size() == 1, Response.TOO_MANY_ACTION);

        // store the one and only mapped gameAction into cmd
        GameAction gameAction = intersection.stream().findFirst().orElse(null);
        cmd.setGameAction(gameAction);
    }

}
