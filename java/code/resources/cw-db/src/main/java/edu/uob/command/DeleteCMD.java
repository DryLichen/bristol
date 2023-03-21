package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.comman.Utils;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;

import java.io.File;
import java.util.List;

public class DeleteCMD extends DBcmd {
    @Override
    public String query(DBServer s) throws DBException {
        FileIO fileIO = new FileIO();
        File tableFile = Utils.getTableFile(s, fileIO, getTableNames().get(0));

        // select tuples according to conditions
        Condition condition = getCondition();


        return "[OK]";
    }
}
