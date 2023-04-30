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
    private HashSet<String> builtInAction = new HashSet<>();

    public ActionData() {
        builtInAction.addAll(Arrays.asList("goto", "look", "drop",
                "inv", "inventory", "get", "health"));
    }

    public HashMap<String, HashSet<GameAction>> getActionMap() {
        return actionMap;
    }

    public HashSet<String> getBuiltInAction() {
        return builtInAction;
    }
}
