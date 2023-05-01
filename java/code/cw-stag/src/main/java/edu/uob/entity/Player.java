package edu.uob.entity;

import java.util.HashSet;

public class Player extends Character {
    private HashSet<Artefact> inventory = new HashSet<>();
    private Integer health;

    public Player(String name, String description) {
        super(name, description);
    }

    public HashSet<Artefact> getInventory() {
        return inventory;
    }

    public Integer getHealth() {
        return health;
    }
}
