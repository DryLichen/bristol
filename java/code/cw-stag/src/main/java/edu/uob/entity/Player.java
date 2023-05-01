package edu.uob.entity;

import java.util.HashSet;

public class Player extends Character {
    private HashSet<Artefact> inventory = new HashSet<>();
    private Integer health = 3;

    public Player(String name, String description) {
        super(name, description);
    }

    public HashSet<Artefact> getInventory() {
        return inventory;
    }

    public Integer getHealth() {
        return health;
    }

    /**
     * @return false if reducing health successfully, true when player is dead
     */
    public boolean consumeHealth() {
        // reset player's health to 3 if player is dead
        if (health == 1) {
            health = 3;
            return true;
        }

        health -= 1;
        return false;
    }

    public void produceHealth() {
        // the maximum of health is 3
        if (health == 3) {
            return;
        }

        health += 1;
    }
}
