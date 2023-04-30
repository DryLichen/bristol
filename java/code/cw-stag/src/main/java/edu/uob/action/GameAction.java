package edu.uob.action;

import java.util.HashSet;

public class GameAction {

    private HashSet<String> triggerSet = new HashSet<>();
    private HashSet<String> subjectSet = new HashSet<>();
    private HashSet<String> consumeSet = new HashSet<>();
    private HashSet<String> produceSet = new HashSet<>();

    public HashSet<String> getTriggerSet() {
        return triggerSet;
    }

    public HashSet<String> getSubjectSet() {
        return subjectSet;
    }

    public HashSet<String> getConsumeSet() {
        return consumeSet;
    }

    public HashSet<String> getProduceSet() {
        return produceSet;
    }
}
