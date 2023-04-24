package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.comman.Utils;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;
import edu.uob.table.Relation;
import edu.uob.table.Tuple;

import java.io.File;
import java.util.LinkedList;
import java.util.StringJoiner;

public class JoinCMD extends DBcmd {
    @Override
    public String query(DBServer s) throws DBException {
        StringJoiner joiner = new StringJoiner("");
        joiner.add("[OK]\r\n");

        // get table files
        FileIO fileIO = new FileIO();
        File table1 = Utils.getTableFile(s, fileIO, getTableNames().get(0));
        File table2 = Utils.getTableFile(s, fileIO, getTableNames().get(1));

        // get relations from two table files
        Relation relation1 = fileIO.getRelationFromFile(table1);
        Relation relation2 = fileIO.getRelationFromFile(table2);
        LinkedList<String> attributes1 = relation1.getAttributes();
        LinkedList<String> attributes2 = relation2.getAttributes();
        LinkedList<Tuple> tuples1 = relation1.getTuples();
        LinkedList<Tuple> tuples2 = relation2.getTuples();

        // find the indexes of joined attributes in original relations
        int joinIndex1 = Utils.getAttributeIndex(attributes1, getColumnNames().get(0));
        int joinIndex2 = Utils.getAttributeIndex(attributes2, getColumnNames().get(1));

        // set attributes for joined relation
        Relation joinRelation = new Relation();
        LinkedList<String> joinAttributes = joinRelation.getAttributes();
        joinAttributes.add("id");
        // skip the id attribute
        for (int i = 1; i < attributes1.size(); i++) {
            if (i == joinIndex1) {
                continue;
            }
            joinAttributes.add(relation1.getName() + "." + attributes1.get(i));
        }
        for (int i = 1; i < attributes2.size(); i++) {
            if (i == joinIndex2) {
                continue;
            }
            joinAttributes.add(relation2.getName() + "." + attributes2.get(i));
        }

        // set tuples for joined relation
        LinkedList<Tuple> joinTuples = joinRelation.getTuples();
        for (Tuple t1 : tuples1) {
            for (Tuple t2 : tuples2) {
                if (joinIndex1 == 0) {
                    // 1. join attribute of the first relation is id
                    if (joinIndex2 == 0) {
                        // 1.1. join attribute of the second relation is id
                        if (Utils.isEqual(t1.getPrimaryId().toString(),
                                t2.getPrimaryId().toString())) {
                            joinTuples.add(setTupleFromOrigin(t1, t2, joinIndex1, joinIndex2));
                        }
                    } else {
                        // 1.2. join attribute of the second relation isn't id
                        if (Utils.isEqual(t1.getPrimaryId().toString(),
                                t2.getData().get(joinIndex2 - 1))) {
                            joinTuples.add(setTupleFromOrigin(t1, t2, joinIndex1, joinIndex2));
                        }
                    }
                } else {
                    // 2. when join attribute of the first relation isn't id
                    // 2.1. join attribute of the second relation is id
                    if (joinIndex2 == 0) {
                        if (Utils.isEqual(t1.getData().get(joinIndex1 - 1), t2.getPrimaryId().toString())) {
                            joinTuples.add(setTupleFromOrigin(t1, t2, joinIndex1, joinIndex2));
                        }
                    } else {
                        // 2.2. join attribute of the second relation isn't id
                        if (Utils.isEqual(t1.getData().get(joinIndex1 - 1),
                                t2.getData().get(joinIndex2 - 1))) {
                            joinTuples.add(setTupleFromOrigin(t1, t2, joinIndex1, joinIndex2));
                        }
                    }
                }

            }
        }
        // set id for join tuple list
        int id = 1;
        for (Tuple joinTuple : joinTuples) {
            joinTuple.setPrimaryId(id++);
        }

        joinRelation.fillJoiner(joiner);
        return joiner.toString();
    }

    private Tuple setTupleFromOrigin(Tuple t1, Tuple t2, int joinIndex1, int joinIndex2) {
        Tuple tuple = new Tuple();

        LinkedList<String> data = new LinkedList<>();
        if (joinIndex1 == 0) {
            data.addAll(t1.getData());
        } else {
            LinkedList<String> copyData1 = new LinkedList<>(t1.getData());
            copyData1.remove(joinIndex1 - 1);
            data.addAll(copyData1);
        }

        if (joinIndex2 == 0) {
            data.addAll(t2.getData());
        } else {
            LinkedList<String> copyData2 = new LinkedList<>(t2.getData());
            copyData2.remove(joinIndex2 - 1);
            data.addAll(copyData2);
        }

        tuple.setData(data);
        return tuple;
    }
}
