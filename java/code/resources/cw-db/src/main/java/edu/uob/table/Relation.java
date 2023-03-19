package edu.uob.table;

import java.util.LinkedList;

public class Relation {
    private String name;
    // attributes include id
    private LinkedList<String> attributes = new LinkedList<>();
    private LinkedList<Tuple> tuples = new LinkedList<>();

    public Relation() {
    }

    public Relation(String name, LinkedList<String> attributes, LinkedList<Tuple> tuples) {
        this.name = name;
        this.attributes = attributes;
        this.tuples = tuples;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(LinkedList<String> attributes) {
        this.attributes = attributes;
    }

    public LinkedList<Tuple> getTuples() {
        return tuples;
    }

    public void setTuples(LinkedList<Tuple> tuples) {
        this.tuples = tuples;
    }
}
