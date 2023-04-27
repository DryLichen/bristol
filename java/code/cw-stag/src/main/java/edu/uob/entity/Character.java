package edu.uob.entity;

import java.util.HashSet;

public class Character extends GameEntity {
    private HashSet<Artefact> inventory;

    public Character(String name, String description) {
        super(name, description);
    }
}
