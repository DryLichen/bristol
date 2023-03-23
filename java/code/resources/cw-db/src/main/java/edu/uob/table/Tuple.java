package edu.uob.table;

import java.util.LinkedList;
import java.util.StringJoiner;

/**
 * use String to store every datatype
 * when it comes to analyze, convert datatype manually
 */
public class Tuple {

    private Integer primaryId;
    private LinkedList<String> data = new LinkedList<>();

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

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\t", "", "\r\n");
        if(primaryId != null) {
            joiner.add(primaryId.toString());
        }
        for (String item : data) {
            joiner.add(item);
        }

        return joiner.toString();
    }
}
