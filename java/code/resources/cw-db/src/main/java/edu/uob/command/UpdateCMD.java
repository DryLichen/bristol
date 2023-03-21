package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.comman.Utils;
import edu.uob.exception.DBException;

import java.io.File;

public class UpdateCMD extends DBcmd {

    /**
     * Potentially mutates database server state
     * @return result of query
     */
    @Override
    public String query(DBServer s) throws DBException {
        FileIO fileIO = new FileIO();
        File tableFile = Utils.getTableFile(s, fileIO, getTableNames().get(0));



        return "[OK]";
    }
}
