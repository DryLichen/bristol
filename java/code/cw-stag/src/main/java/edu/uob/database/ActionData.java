package edu.uob.database;

import edu.uob.action.GameAction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * store all the actions
 */
public class ActionData {
    // every trigger is mapped to an action
    private HashMap<String, HashSet<GameAction>> actionMap = new HashMap<>();
    private HashSet<String> builtIn = new HashSet<>();

    public ActionData() {
        builtIn.addAll(Arrays.asList("health", "goto", "look", "drop",
                "inv", "inventory", "get"));
    }

    public HashMap<String, HashSet<GameAction>> getActionMap() {
        return actionMap;
    }

    public HashSet<String> getBuiltIn() {
        return builtIn;
    }
}
