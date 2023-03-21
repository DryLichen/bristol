package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.comman.Utils;
import edu.uob.exception.DBException;
import edu.uob.table.Relation;
import edu.uob.table.Tuple;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public class SelectCMD extends DBcmd {

    /**
     * Potentially mutates database server state
     * @return result of query
     */
    @Override
    public String query(DBServer s) throws DBException {
        StringJoiner joiner = new StringJoiner("");
        joiner.add("[OK]\r\n");

        FileIO fileIO = new FileIO();
        File tableFile = Utils.getTableFile(s, fileIO, getTableNames().get(0));

        // get relation from table file
        Relation relation = fileIO.getRelationFromFile(tableFile);
        LinkedList<String> attributes = relation.getAttributes();
        LinkedList<Tuple> tuples = relation.getTuples();

        Condition condition = this.getCondition();
        List<String> columns = this.getColumnNames();
        if (condition == null) {
            // without condition
            if (columns == null) {
                // wild case
                relation.fillJoiner(joiner);
            } else {
                // general case
                // find the indexes of other attributes that are not chosen ascending
                ArrayList<Integer> otherIndex = new ArrayList<>();
                for (int i = 0; i < attributes.size(); i++) {
                    String attribute = attributes.get(i);
                    if (!columns.stream().anyMatch(column -> column.equalsIgnoreCase(attribute))) {
                        otherIndex.add(i);
                    }
                }

                // delete other attributes that are not chosen
                for (int i = otherIndex.size() - 1; i >= 0; i--) {
                    attributes.remove(otherIndex.get(i).intValue());
                }

                // delete the corresponding tuple data
                for (Tuple tuple : tuples) {
                    for (int i = otherIndex.size() - 1; i >= 0; i--) {
                        if (otherIndex.get(i) == 0) {
                            tuple.setPrimaryId(null);
                        } else {
                            tuple.getData().remove(otherIndex.get(i).intValue() - 1);
                        }
                    }
                }

                relation.fillJoiner(joiner);
            }

        } else {
            // with condition
            if (columns == null) {
                // wild case
                // delete other tuples that are not chosen

                relation.fillJoiner(joiner);
            } else {
                // general case
                // delete other tuples that are not chosen

                // delete other attributes that are not chosen

                relation.fillJoiner(joiner);
            }
        }



        return joiner.toString();
    }

}
