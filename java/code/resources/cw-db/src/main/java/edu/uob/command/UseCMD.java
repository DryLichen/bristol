package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class UseCMD extends DBcmd {
    /**
     * Potentially mutates Database server state
     * @return result of query
     */
    @Override
    public String query(DBServer s) throws DBException {
        // check if the database we are trying to use actually exists
        String storageFolderPath = s.getStorageFolderPath();
        FileIO fileIO = new FileIO();
        File database = fileIO.getDatabase(storageFolderPath, getDBname());

        Assert.fileExists(database);
        s.setDatabaseName(getDBname());
        return "[OK]";
    }


}
