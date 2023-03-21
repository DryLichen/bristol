package edu.uob.table;

import java.util.LinkedList;
import java.util.StringJoiner;

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

    public void deleteAttribute(int index) {
        attributes.remove(index);
        for (Tuple tuple : tuples) {
            if (index == 0) {
                tuple.setPrimaryId(null);
            } else {
                tuple.getData().remove(index - 1);
            }
        }

    }

    public void addAttribute(String attribute) {
        attributes.add(attribute);
        for (Tuple tuple : tuples) {
            tuple.getData().add("NULL");
        }
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

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\t", "", "\r\n");
        for (String attribute : attributes) {
            joiner.add(attribute);
        }
        return joiner.toString();
    }

    public void fillJoiner(StringJoiner joiner) {
        joiner.add(this.toString());
        for (Tuple tuple : this.getTuples()) {
            joiner.add(tuple.toString());
        }
    }
}
