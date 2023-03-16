package edu.uob.command;

import edu.uob.DBServer;

import java.util.ArrayList;

public class InsertCMD extends DBcmd {
    private ArrayList<String> valueList;

    public void setValueList(ArrayList<String> valueList) {
        this.valueList = valueList;
    }

    public ArrayList<String> getValueList() {
        return valueList;
    }

    @Override
    public String query(DBServer s) {
        return null;
    }
}
