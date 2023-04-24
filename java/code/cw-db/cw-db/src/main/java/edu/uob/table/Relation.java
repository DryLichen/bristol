package edu.uob.table;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringJoiner;

public class Relation {
    private String name;
    // attributes include id
    private LinkedList<String> attributes = new LinkedList<>();
    private LinkedList<Tuple> tuples = new LinkedList<>();

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

    /**
     * delete tuples having id in idSet
     */
    public void deleteTuples(HashSet<Integer> idSet) {
        Iterator<Tuple> iterator = tuples.iterator();
        while (iterator.hasNext()) {
            Tuple tuple = iterator.next();
            for (Integer id : idSet) {
                if (tuple.getPrimaryId() == id) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * keep tuples having id in idSet and delete others
     */
    public void keepTuples(HashSet<Integer> idSet) {
        HashSet<Integer> allIds = new HashSet<>();
        // get set of all the primary keys
        for (Tuple tuple : tuples) {
            allIds.add(tuple.getPrimaryId());
        }

        allIds.removeAll(idSet);
        deleteTuples(allIds);
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
