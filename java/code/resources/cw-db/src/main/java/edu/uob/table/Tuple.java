package edu.uob.table;

import java.util.LinkedList;

/**
 * use String to store every datatype
 * when it comes to analyze, convert datatype manually
 */
public class Tuple {
    private Integer primaryId;
    private LinkedList<String> data = new LinkedList<>();

    public Tuple() {
    }

    public Tuple(Integer primaryId, LinkedList<String> data) {
        this.primaryId = primaryId;
        this.data = data;
    }

    public Integer getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(Integer primaryId) {
        this.primaryId = primaryId;
    }

    public LinkedList<String> getData() {
        return data;
    }

    public void setData(LinkedList<String> data) {
        this.data = data;
    }
}
