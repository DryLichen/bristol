package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.comman.ConditionUtils;
import edu.uob.comman.Utils;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;
import edu.uob.table.Relation;
import edu.uob.table.Tuple;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class UpdateCMD extends DBcmd {
    private List<String> valueList;

    /**
     * update values in table
     * @return [OK] if success, otherwise return exception message
     */
    @Override
    public String query(DBServer s) throws DBException {
        FileIO fileIO = new FileIO();
        File tableFile = Utils.getTableFile(s, fileIO, getTableNames().get(0));
        // get relation from table file
        Relation relation = fileIO.getRelationFromFile(tableFile);
        LinkedList<String> attributes = relation.getAttributes();
        LinkedList<Tuple> tuples = relation.getTuples();

        // get command information
        List<String> columns = this.getColumnNames();
        List<String> valueList = this.getValueList();
        Condition condition = this.getCondition();
        ConditionUtils conditionUtils = new ConditionUtils();
        HashSet<Integer> idSet = conditionUtils.getTupleIds(condition, relation);

        // update data
        for (int i = 0; i < columns.size(); i++) {
            // get indexes of attribute
            int index = Utils.getAttributeIndex(attributes, columns.get(i));
            // can't modify id
            Assert.isTrue(index != 0, Response.FORBID_MODIFY_ID);

            // iterate the chosen tuples
            for (Integer id : idSet) {
                for (Tuple tuple : tuples) {
                    if (tuple.getPrimaryId() == id) {
                        tuple.getData().set(index - 1, valueList.get(i));
                    }
                }
            }

        }

        // write back to file
        fileIO.setRelationToFile(relation,
                fileIO.getDatabase(s.getStorageFolderPath(), s.getSpecifiedDb()));

        return "[OK]";
    }

    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }

    public List<String> getValueList() {
        return valueList;
    }
}
