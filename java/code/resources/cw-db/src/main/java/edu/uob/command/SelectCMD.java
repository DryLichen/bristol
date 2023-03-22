package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.comman.ConditionUtils;
import edu.uob.comman.Utils;
import edu.uob.exception.DBException;
import edu.uob.table.Relation;
import edu.uob.table.Tuple;

import java.io.File;
import java.util.*;

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

        // get command information
        Condition condition = this.getCondition();
        List<String> columns = this.getColumnNames();

        if (condition == null) {
            // without condition
            if (columns == null) {
                // wild case
                relation.fillJoiner(joiner);
            } else {
                // general case
                Relation newRelation = setAttrInOrder(relation, columns);
                newRelation.fillJoiner(joiner);
            }
        } else {
            // with condition
            ConditionUtils conditionUtils = new ConditionUtils();
            HashSet<Integer> idSet = conditionUtils.getTupleIds(condition, relation);
            // only keep the chosen tuples
            relation.keepTuples(idSet);
            if (columns == null) {
                // wild case
                relation.fillJoiner(joiner);
            } else {
                // general case
                // get a new relation with chosen attributes
                Relation newRelation = setAttrInOrder(relation, columns);
                newRelation.fillJoiner(joiner);
            }
        }

        return joiner.toString();
    }

    /**
     * @return a new relation with attributes and tuples in order of select command
     */
    private Relation setAttrInOrder(Relation relation, List<String> columns) throws DBException {
        LinkedList<String> attributes = relation.getAttributes();
        LinkedList<Tuple> tuples = relation.getTuples();

        // general case
        // create a new relation containing chosen attributes
        Relation newRelation = new Relation();
        newRelation.setName(relation.getName());
        newRelation.setAttributes(new LinkedList<>(columns));

        // create the same number of tuples in new relation
        LinkedList<Tuple> newTuples = newRelation.getTuples();
        for (Tuple tuple : tuples) {
            Tuple newTuple = new Tuple();
            newTuples.add(newTuple);
        }

        // fill data in new tuples in order of new attributes
        for (String newAttribute : newRelation.getAttributes()) {
            int index = Utils.getAttributeIndex(attributes, newAttribute);
            if (index == 0) {
                // attribute is ID
                for (int i = 0; i < newTuples.size(); i++) {
                    newTuples.get(i).getData().add(tuples.get(i).getPrimaryId().toString());
                }
            } else {
                for (int i = 0; i < newTuples.size(); i++) {
                    newTuples.get(i).getData().add(tuples.get(i).getData().get(index - 1));
                }
            }
        }

        return newRelation;
    }

}
