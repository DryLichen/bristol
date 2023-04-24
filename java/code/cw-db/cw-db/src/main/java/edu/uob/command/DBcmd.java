package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.exception.DBException;

import java.util.List;

public abstract class DBcmd {
    protected Condition condition;
    protected List<String> columnNames;
    protected List<String> tableNames;
    protected String DBname;
    protected String commandType;

    public DBcmd() {
    }

    /**
     * Potentially mutates database server state
     * @return result of query
     */
    public abstract String query(DBServer s) throws DBException;

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    public String getDBname() {
        return DBname;
    }

    public void setDBname(String DBname) {
        this.DBname = DBname;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }
}
