package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;

import java.io.File;

public class UseCMD extends DBcmd {

    /**
     * change the databaseName field in DB server
     * @return [OK] or DBException
     */
    @Override
    public String query(DBServer s) throws DBException {
        // check if the database we are trying to use actually exists
        String root = s.getStorageFolderPath();
        FileIO fileIO = new FileIO();
        File database = fileIO.getDatabase(root, getDBname().toLowerCase());

        Assert.fileExists(database, Response.DB_NOT_EXIST);
        s.setSpecifiedDb(getDBname());
        return "[OK]";
    }

}
