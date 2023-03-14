package edu.uob.table;

import java.util.LinkedList;

public class Relation {
    private String name;
    private LinkedList<Attribute> attributes;
    private LinkedList<Tuple> tuples;

    public Relation() {
    }

    public Relation(String name, LinkedList<Attribute> attributes, LinkedList<Tuple> tuples) {
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

    public LinkedList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(LinkedList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public LinkedList<Tuple> getTuples() {
        return tuples;
    }

    public void setTuples(LinkedList<Tuple> tuples) {
        this.tuples = tuples;
    }
}
