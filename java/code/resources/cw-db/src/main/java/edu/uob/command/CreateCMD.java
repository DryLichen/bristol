package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;
import edu.uob.table.Relation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class CreateCMD extends DBcmd {

    /**
     * create database or table file
     * @return [OK] or DBException
     */
    @Override
    public String query(DBServer s) throws DBException {
        FileIO fileIO = new FileIO();
        String root = s.getStorageFolderPath();

        // 1. create a database
        if (this.getDBname() != null) {
            boolean flag = fileIO.createDatabase(root, this.getDBname().toLowerCase());
            // can't create an existing database
            Assert.isTrue(flag, Response.CREATE_EXIST_DB);
            return "[OK]";
        }

        // 2. create a table
        // a database should be specified before
        String specifiedDb = s.getSpecifiedDb();
        Assert.notNull(specifiedDb, Response.DB_NOT_SPECIFIED);

        // can't create an existing table
        boolean flag = false;
        try {
            flag = fileIO.createTable(root, specifiedDb, getTableNames().get(0).toLowerCase());
        } catch (IOException e) {
            throw new DBException(Response.CREATE_TABLE_FAIL);
        }
        Assert.isTrue(flag, Response.CREATE_EXIST_TABLE);

        // create attributes if they are initiated
        if (this.getColumnNames() != null) {
            createAttrList(fileIO, root, specifiedDb);
        }

        return "[OK]";
    }

    private void createAttrList(FileIO fileIO, String root, String specifiedDb) throws DBException {
        Relation relation = new Relation();
        relation.setName(getTableNames().get(0));
        relation.setAttributes(new LinkedList<>(getColumnNames()));
        // add id column
        relation.getAttributes().addFirst("id");
        fileIO.setRelationToFile(relation, fileIO.getDatabase(root, specifiedDb));
    }

}
