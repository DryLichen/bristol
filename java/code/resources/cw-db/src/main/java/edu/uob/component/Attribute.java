package edu.uob.component;

import java.util.LinkedList;

/**
 * store data in String type
 */
public class Attribute {
    private String name;
    private LinkedList<String> data;

    public Attribute() {
    }

    public Attribute(String name, LinkedList<String> data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<String> getData() {
        return data;
    }

    public void setData(LinkedList<String> data) {
        this.data = data;
    }
}
