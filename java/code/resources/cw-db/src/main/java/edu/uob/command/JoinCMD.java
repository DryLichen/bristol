package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;
import edu.uob.table.Relation;
import edu.uob.table.Tuple;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public class JoinCMD extends DBcmd {
    @Override
    public String query(DBServer s) throws DBException {
        StringJoiner joiner = new StringJoiner("\t", "", "\r\n");

        // get table files
        String root = s.getStorageFolderPath();
        String specifiedDb = s.getSpecifiedDb();
        Assert.notNull(specifiedDb, Response.DB_NOT_SPECIFIED);
        FileIO fileIO = new FileIO();
        File table1 = fileIO.getTable(root, specifiedDb, getTableNames().get(0));
        File table2 = fileIO.getTable(root, specifiedDb, getTableNames().get(1));
        Assert.fileExists(table1, Response.TABLE_NOT_EXIST);
        Assert.fileExists(table2, Response.TABLE_NOT_EXIST);

        // get relations from two table files
        Relation relation1 = fileIO.getRelationFromFile(table1);
        Relation relation2 = fileIO.getRelationFromFile(table2);
        LinkedList<String> attributes1 = relation1.getAttributes();
        LinkedList<String> attributes2 = relation2.getAttributes();
        LinkedList<Tuple> tuples1 = relation1.getTuples();
        LinkedList<Tuple> tuples2 = relation2.getTuples();

        // get the attributes expect joined attributes
        int joinIndex1 = getJoinIndex(getColumnNames().get(0), attributes1);
        int joinIndex2 = getJoinIndex(getColumnNames().get(1), attributes2);

        // set attributes for joined relation
        Relation joinRelation = new Relation();
        LinkedList<String> joinAttributes = joinRelation.getAttributes();
        joinAttributes.add("id");
        for (int i = 0; i < attributes1.size(); i++) {
            if (i == joinIndex1) {
                continue;
            }
            joinAttributes.add(attributes1.get(i));
        }
        for (int i = 0; i < attributes2.size(); i++) {
            if (i == joinIndex2) {
                continue;
            }
            joinAttributes.add(attributes2.get(i));
        }

        // set tuples for joined relation
//        for (Tuple tuple : tuples1) {
//            tuple.getData().get()
//        }


        return null;
    }

//    private LinkedList<String> getRemain(String joinColumn, LinkedList<String> origin) {
//        LinkedList<String> remain = new LinkedList<>(origin);
//        for (String str : remain) {
//            if (str.equalsIgnoreCase(joinColumn)) {
//                remain.remove(str);
//            }
//        }
//
//        return remain;
//    }

    private int getJoinIndex(String joinColumn, LinkedList<String> attributes) throws DBException {
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).equalsIgnoreCase(joinColumn)) {
                return i;
            }
        }
        throw new DBException(Response.ATTR_NOT_EXIST);
    }
}
