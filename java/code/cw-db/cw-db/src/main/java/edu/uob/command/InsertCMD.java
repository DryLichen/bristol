package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.comman.Utils;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;
import edu.uob.table.Relation;
import edu.uob.table.Tuple;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class InsertCMD extends DBcmd {
    private ArrayList<String> valueList;

    public void setValueList(ArrayList<String> valueList) {
        this.valueList = valueList;
    }

    @Override
    public String query(DBServer s) throws DBException {
        // get table file
        FileIO fileIO = new FileIO();
        File tableFile = Utils.getTableFile(s, fileIO, getTableNames().get(0));

        // get relation from table file
        Relation relation = fileIO.getRelationFromFile(tableFile);
        LinkedList<String> attributes = relation.getAttributes();
        LinkedList<Tuple> tuples = relation.getTuples();
        // table must have attributes
        Assert.isTrue(attributes.size() > 0, Response.EMPTY_TABLE);
        // check if the number of values is coherent with the attributes
        Assert.isTrue(attributes.size() - 1 == valueList.size(), Response.NOT_COMPATIBLE_VALUES);

        // insert data to relation
        Tuple tuple = new Tuple();
        tuple.setData(new LinkedList<>(valueList));

        // set primary key from 1 if there is no tuple
        if (tuples.size() == 0) {
            tuple.setPrimaryId(1);
        } else {
            tuple.setPrimaryId(tuples.get(tuples.size() - 1).getPrimaryId() + 1);
        }
        tuples.add(tuple);
        fileIO.setRelationToFile(relation, fileIO.getDatabase(s.getStorageFolderPath(), s.getSpecifiedDb()));

        return "[OK]";
    }

}
