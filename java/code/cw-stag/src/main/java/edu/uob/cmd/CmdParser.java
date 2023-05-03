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

/**
 * parser for cmd
 */
public class CmdParser {
    private ActionData actionData;
    private EntityData entityData;

    public CmdParser(ActionData actionData, EntityData entityData) {
        this.actionData = actionData;
        this.entityData = entityData;
    }

    /**
     * check syntax correctness of cmd
     * @return parsed cmd
     * @throws STAGException handled by GameServer
     */
    public Cmd parseCmd(String command) throws STAGException {
        CmdTokenizer cmdTokenizer = new CmdTokenizer(actionData, entityData);
        Cmd cmd = cmdTokenizer.tokenizeCommand(command);

        // check if cmd is valid in syntax
        ArrayList<String> cmdBuiltIn = cmd.getBuiltInAction();
        ArrayList<String> cmdActions = cmd.getActionList();

        // case1: cmd contains built-in action
        Assert.isTrue(cmdBuiltIn.size() <= 1, Response.TOO_MANY_ACTION);
        if (cmdBuiltIn.size() == 1) {
            parseBuiltIn(cmdActions, cmdBuiltIn, cmd);
        }

        // case2: cmd contains normal actions
        if (cmdBuiltIn.size() == 0) {
            parseAction(cmdActions, cmd);
        }

        return cmd;
    }

    /**
     * check syntax correctness of built-in commands
     */
    private void parseBuiltIn(ArrayList<String> cmdActions, ArrayList<String> cmdBuiltIn, Cmd cmd) throws STAGException {
        Assert.isTrue(cmdActions.size() == 0, Response.TOO_MANY_ACTION);
        String builtIn = cmdBuiltIn.get(0);

        if ("goto".equalsIgnoreCase(builtIn)) {
            checkEntitiesNum(cmd, 0,0, 0, 1);
        } else if ("look".equalsIgnoreCase(builtIn) || "inv".equalsIgnoreCase(builtIn) ||
                "inventory".equalsIgnoreCase(builtIn) || "health".equalsIgnoreCase(builtIn)) {
            checkEntitiesNum(cmd, 0, 0, 0, 0);
        } else if ("drop".equalsIgnoreCase(builtIn) || "get".equalsIgnoreCase(builtIn)) {
            checkEntitiesNum(cmd, 0, 0, 1, 0);
        }
    }

    /**
     * check if the number of each type of entities is correct for different actions
     */
    private void checkEntitiesNum(Cmd cmd, int furnNum, int charaNum, int arteNum, int locNum) throws STAGException {
        Assert.isTrue(cmd.getFurnitureList().size() == furnNum, Response.TOO_MANY_ENTITY);
        Assert.isTrue(cmd.getCharacterList().size() == charaNum, Response.TOO_MANY_ENTITY);
        Assert.isTrue(cmd.getArtefactList().size() == arteNum, Response.TOO_MANY_ENTITY);
        Assert.isTrue(cmd.getLocationList().size() == locNum, Response.ONE_LOCATION);
    }

    /**
     * check syntax correctness of normal actions
     */
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

        // no extraneous entities
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
        Assert.isTrue(intersection.size() == 1, Response.WRONG_COMMAND);

        // store the one and only mapped gameAction into cmd
        GameAction gameAction = intersection.stream().findFirst().orElse(null);
        cmd.setGameAction(gameAction);
    }

}
