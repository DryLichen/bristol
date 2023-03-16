package edu.uob.command;

import edu.uob.DBServer;

public class AlterCMD extends DBcmd {
    private String alterationType;

    public void setAlterationType(String alterationType) {
        this.alterationType = alterationType;
    }

    public String getAlterationType() {
        return alterationType;
    }

    @Override
    public String query(DBServer s) {
        return null;
    }
}
