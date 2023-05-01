package edu.uob.action;

import edu.uob.entity.GameEntity;

import java.util.HashSet;

public class GameAction {

    private HashSet<String> triggerSet = new HashSet<>();
    private HashSet<GameEntity> subjectSet = new HashSet<>();
    private HashSet<GameEntity> consumeSet = new HashSet<>();
    private HashSet<GameEntity> produceSet = new HashSet<>();
    private String narration;

    private boolean consumeHealth;
    private boolean produceHealth;

    public HashSet<String> getTriggerSet() {
        return triggerSet;
    }

    public HashSet<GameEntity> getSubjectSet() {
        return subjectSet;
    }

    public HashSet<GameEntity> getConsumeSet() {
        return consumeSet;
    }

    public HashSet<GameEntity> getProduceSet() {
        return produceSet;
    }

    public String getNarration() {
        return narration;
    }

    public boolean isConsumeHealth() {
        return consumeHealth;
    }

    public boolean isProduceHealth() {
        return produceHealth;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public void setConsumeHealth(boolean consumeHealth) {
        this.consumeHealth = consumeHealth;
    }

    public void setProduceHealth(boolean produceHealth) {
        this.produceHealth = produceHealth;
    }
}
