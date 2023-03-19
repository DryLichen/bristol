package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;
import edu.uob.table.Relation;
import edu.uob.table.Tuple;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class AlterCMD extends DBcmd {
    private String alterationType;

    public void setAlterationType(String alterationType) {
        this.alterationType = alterationType;
    }

    @Override
    public String query(DBServer s) throws DBException {
        String root = s.getStorageFolderPath();
        String specifiedDb = s.getSpecifiedDb();
        Assert.notNull(specifiedDb, Response.DB_NOT_SPECIFIED);
        FileIO fileIO = new FileIO();
        File tableFile = fileIO.getTable(root, specifiedDb, getTableNames().get(0));
        Assert.fileExists(tableFile, Response.TABLE_NOT_EXIST);

        Relation relation = fileIO.getRelationFromFile(tableFile);
        LinkedList<String> attributes = relation.getAttributes();
        LinkedList<Tuple> tuples = relation.getTuples();
        String alterAttribute = this.getColumnNames().get(0);

        // add an attribute
        if ("ADD".equalsIgnoreCase(alterationType)) {
            // modify attributes in relation
            // add id attribute if it doesn't exist
            if (attributes.size() == 0) {
                attributes.add("id");
            }
            // cannot add attributes which already exist
            for (String attribute : attributes) {
                Assert.isTrue(!attribute.equalsIgnoreCase(alterAttribute), Response.DUPLICATE_ATTR);
            }
            attributes.add(alterAttribute);

            // modify tuples
            // fill new column with NULL
            for (Tuple tuple : tuples) {
                LinkedList<String> data = tuple.getData();
                data.add("NULL");
            }
        } else {
            // drop an attribute
            // can't drop id
            Assert.isTrue(!"id".equalsIgnoreCase(alterAttribute), Response.FORBID_DROP_ID);
            // attributes cannot be empty
            Assert.isTrue(attributes.size() != 0, Response.EMPTY_TABLE);
            // modify attributes
            int index = -1;
            for (int i = 0; i < attributes.size(); i++) {
                if (alterAttribute.equalsIgnoreCase(attributes.get(i))) {
                    index = i;
                }
            }
            // attribute to be dropped must exist
            Assert.isTrue(index != -1, Response.ATTR_NOT_EXIST);
            attributes.remove(index);

            // modify tuples if there are tuples
            for (Tuple tuple : tuples) {
                LinkedList<String> data = tuple.getData();
                data.remove(index - 1);
            }
        }

        fileIO.setRelationToFile(relation, fileIO.getDatabase(root, specifiedDb));
        return "[OK]";
    }

}
