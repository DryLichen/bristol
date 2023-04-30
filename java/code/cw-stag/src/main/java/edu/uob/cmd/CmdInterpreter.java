package edu.uob.cmd;

import edu.uob.database.ActionData;
import edu.uob.database.EntityData;

public class CmdInterpreter {
    private ActionData actionData;
    private EntityData entityData;

    public CmdInterpreter(ActionData actionData, EntityData entityData) {
        this.actionData = actionData;
        this.entityData = entityData;
    }

    public void interpretCmd(String command) {

    }
}
