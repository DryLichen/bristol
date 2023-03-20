package edu.uob.command;

import java.util.ArrayList;
import java.util.List;

public class Condition {
    // all the three fields should be null if it's not a pure condition.
    // pure condition means there are only a comparison expression
    private String attributeName;
    private String comparator;
    private String value;
    // only add items when it's not a pure condition
    private List<Condition> conditions = new ArrayList<>();
    // list of bool operators in the same level
    private List<String> operators = new ArrayList<>();

    public Condition() {
    }

    public Condition(String attributeName, String comparator, String value) {
        this.attributeName = attributeName;
        this.comparator = comparator;
        this.value = value;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public List<String> getOperators() {
        return operators;
    }

    public void setOperators(List<String> operators) {
        this.operators = operators;
    }
}
