package edu.uob.parser;

import edu.uob.database.ActionData;

import java.io.File;

/**
 * parse actions file and store data into database
 */
public class ActionFileParser {
    private File actionsFile;
    private ActionData actionData;

    public ActionFileParser(File actionsFile, ActionData actionData) {
        this.actionsFile = actionsFile;
        this.actionData = actionData;
    }

    public void parseActions() {

    }
}
