package edu.uob.cmd;

import edu.uob.action.GameAction;
import edu.uob.database.ActionData;
import edu.uob.database.EntityData;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;
import edu.uob.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;

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
            Assert.isTrue(cmd.getLocationList().size() == 0, Response.ONE_LOCATION);
            return;
        }

        if ("drop".equalsIgnoreCase(builtIn) || "get".equalsIgnoreCase(builtIn)) {
            Assert.isTrue(cmd.getArtefactList().size() == 1, Response.LACK_ENTITY);
            Assert.isTrue(cmd.getLocationList().size() == 0, Response.TOO_MANY_ENTITY);
        }
    }

    private void parseAction(ArrayList<String> cmdActions, Cmd cmd) throws STAGException {
        Assert.isTrue(cmdActions.size() != 0, Response.LACK_ACTION);

        // an action set used to get intersection of every action set
        HashSet<GameAction> intersection = null;
        // get gameAction by searching trigger
        for (int i = 0; i < cmdActions.size(); i++) {
            // initial intersection set
            if (i == 0) {
                intersection = actionData.getActionMap().get(cmdActions.get(0));
            }
            intersection.retainAll(actionData.getActionMap().get(cmdActions.get(i)));
        }

        // 1. check if there are extraneous action
        Assert.isTrue(intersection.size() == 1, Response.TOO_MANY_ACTION);
        GameAction gameAction = intersection.stream().findFirst().orElse(null);

        // 2. there must be at least one entity in the cmd
        Assert.isTrue(cmd.getCmdEntities().size() != 0, Response.LACK_ENTITY);
        // 3. check if there are extraneous entities
        // subjects in gameAction must contain entities in cmd
        boolean flag = gameAction.getSubjectSet().containsAll(cmd.getCmdEntities());
        Assert.isTrue(flag, Response.TOO_MANY_ENTITY);

        cmd.setGameAction(gameAction);
    }

}
