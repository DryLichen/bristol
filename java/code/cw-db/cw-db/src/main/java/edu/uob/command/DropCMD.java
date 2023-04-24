package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;

import java.io.File;
import java.io.IOException;

public class DropCMD extends DBcmd {

    /**
     * drop a database folder or a table file
     * @return [OK] or DBException
     */
    @Override
    public String query(DBServer s) throws DBException {
        String root = s.getStorageFolderPath();
        String specifiedDb = s.getSpecifiedDb();
        FileIO fileIO = new FileIO();

        // 1. drop a database
        if (this.getDBname() != null) {
            File databaseFile = fileIO.getDatabase(root, this.getDBname());
            Assert.fileExists(databaseFile, Response.DB_NOT_EXIST);
            try {
                fileIO.deleteFile(databaseFile);
            } catch (IOException e) {
                throw new DBException(Response.DROP_DB_FAIL);
            }
            return "[OK]";
        }

        // 2. drop a table
        Assert.notNull(specifiedDb, Response.DB_NOT_SPECIFIED);
        File tableFile = fileIO.getTable(root, specifiedDb, this.getTableNames().get(0));
        Assert.fileExists(tableFile, Response.TABLE_NOT_EXIST);
        try {
            fileIO.deleteFile(tableFile);
        } catch (IOException e) {
            throw new DBException(Response.DROP_TABLE_FAIL);
        }
        return "[OK]";
    }
}
