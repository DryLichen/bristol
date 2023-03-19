package edu.uob.command;

import edu.uob.DBServer;

public class UpdateCMD extends DBcmd {

    /**
     * Potentially mutates database server state
     * @return result of query
     */
    @Override
    public String query(DBServer s) {

        return "[OK]";
    }
}
