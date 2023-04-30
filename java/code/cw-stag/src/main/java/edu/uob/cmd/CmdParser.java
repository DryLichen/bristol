package edu.uob.cmd;

import edu.uob.database.ActionData;
import edu.uob.database.EntityData;
import edu.uob.exception.STAGException;

import java.util.ArrayList;

public class CmdParser {
    private ActionData actionData;
    private EntityData entityData;

    public CmdParser(ActionData actionData, EntityData entityData) {
        this.actionData = actionData;
        this.entityData = entityData;
    }

    public void parseCmd(String command) throws STAGException {
        CmdTokenizer cmdTokenizer = new CmdTokenizer(actionData, entityData);
        Cmd cmd = cmdTokenizer.tokenizeCommand(command);

        // check if the syntax of cmd is valid
        ArrayList<String> actionList = cmd.getActionList();


    }
}
