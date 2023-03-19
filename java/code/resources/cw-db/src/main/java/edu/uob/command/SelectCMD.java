package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;
import edu.uob.table.Relation;

import java.io.File;

public class SelectCMD extends DBcmd {

    /**
     * Potentially mutates database server state
     * @return result of query
     */
    @Override
    public String query(DBServer s) throws DBException {
        String queryResult = null;

        String root = s.getStorageFolderPath();
        String specifiedDb = s.getSpecifiedDb();
        Assert.notNull(specifiedDb, Response.DB_NOT_SPECIFIED);
        FileIO fileIO = new FileIO();
        File tableFile = fileIO.getTable(root, specifiedDb, this.getTableNames().get(0));
        Assert.fileExists(tableFile, Response.TABLE_NOT_EXIST);

        Relation relation = fileIO.getRelationFromFile(tableFile);
        // without condition

        // with condition

        return queryResult;
    }
}
