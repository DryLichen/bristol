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
        // cmd contains built-in action
        ArrayList<String> cmdBuiltIn = cmd.getBuiltInAction();
        ArrayList<String> cmdActions = cmd.getActionList();
        Assert.isTrue(cmdBuiltIn.size() <= 1, Response.TOO_MANY_ACTION);
        if (cmdBuiltIn.size() == 1) {
            Assert.isTrue(cmdActions.size() == 0, Response.TOO_MANY_ACTION);
            if ("goto".equalsIgnoreCase(cmdBuiltIn.get(0))) {
                Assert.isTrue(cmd.getArtefactList().size() == 0, Response.TOO_MANY_ENTITY);
                Assert.isTrue(cmd.getFurnitureList().size() == 0, Response.TOO_MANY_ENTITY);
                Assert.isTrue(cmd.getCharacterList().size() == 0, Response.TOO_MANY_ENTITY);
                Assert.isTrue(cmd.getLocationList().size() == 1, Response.ONE_LOCATION);
            }
            if ("look".equalsIgnoreCase(cmdBuiltIn.get(0))) {

            }
        }

        // cmd doesn't contain built-in action
        if (cmdBuiltIn.size() == 0) {
            // then cmd must contain a normal action
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

        return cmd;
    }

    private void parseBuiltIn() {

    }

    private void parseAction() {

    }

}
