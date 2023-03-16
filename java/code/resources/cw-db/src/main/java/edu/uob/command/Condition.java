package edu.uob.command;

public class Condition {
    private String attributeName;
    private String comparator;
    private String value;

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
}
