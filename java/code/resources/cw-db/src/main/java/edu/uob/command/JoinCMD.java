package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;
import edu.uob.table.Relation;

import java.io.File;
import java.util.LinkedList;

public class JoinCMD extends DBcmd {
    @Override
    public String query(DBServer s) throws DBException {
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

        return null;
    }
}
