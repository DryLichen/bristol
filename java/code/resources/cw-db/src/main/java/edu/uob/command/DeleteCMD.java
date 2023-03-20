package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;

import java.io.File;
import java.util.List;

public class DeleteCMD extends DBcmd {
    @Override
    public String query(DBServer s) throws DBException {
        String root = s.getStorageFolderPath();
        String specifiedDb = s.getSpecifiedDb();
        Assert.notNull(specifiedDb, Response.DB_NOT_SPECIFIED);
        FileIO fileIO = new FileIO();
        File tableFile = fileIO.getTable(root, specifiedDb, getTableNames().get(0));
        Assert.fileExists(tableFile, Response.TABLE_NOT_EXIST);

        // select tuples according to conditions
        Condition condition = getCondition();


        return "[OK]";
    }
}
