package edu.uob.action;

import edu.uob.entity.GameEntity;

import java.util.HashSet;

/**
 * normal actions defined in actions.dot
 */
public class GameAction {

    private HashSet<String> triggerSet = new HashSet<>();
    private HashSet<GameEntity> subjectSet = new HashSet<>();
    private HashSet<GameEntity> consumeSet = new HashSet<>();
    private HashSet<GameEntity> produceSet = new HashSet<>();
    private String narration;

    // since health is not GameEntity, here I use bool to store data for health
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
