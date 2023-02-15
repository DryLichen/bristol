package org.example;

import java.util.List;

public class Unit {
    private String name;
    private List<String> topics;

    public Unit(String name, List<String> topics) {
        this.name = name;
        this.topics = topics;
    }

    public String getName() { 
        return this.name; 
    }
    
    public List<String> getTopics() { 
        return this.topics; 
    }
}
