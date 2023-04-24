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
import java.util.LinkedList;

public class AlterCMD extends DBcmd {
    private String alterationType;

    public void setAlterationType(String alterationType) {
        this.alterationType = alterationType;
    }

    @Override
    public String query(DBServer s) throws DBException {
        FileIO fileIO = new FileIO();
        File tableFile = Utils.getTableFile(s, fileIO, getTableNames().get(0));
        // get relation from file
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

            relation.addAttribute(alterAttribute);

        } else {
            // drop an attribute
            // can't drop id
            Assert.isTrue(!"id".equalsIgnoreCase(alterAttribute), Response.FORBID_DROP_ID);
            // attributes cannot be empty
            Assert.isTrue(attributes.size() != 0, Response.EMPTY_TABLE);
            // get the index of the attribute to be deleted
            int index = Utils.getAttributeIndex(attributes, alterAttribute);
            relation.deleteAttribute(index);
        }

        fileIO.setRelationToFile(relation, fileIO.getDatabase(s.getStorageFolderPath(), s.getSpecifiedDb()));
        return "[OK]";
    }

}
