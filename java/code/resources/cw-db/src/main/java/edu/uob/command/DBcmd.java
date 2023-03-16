package edu.uob.command;

import edu.uob.DBServer;

import java.util.List;

public abstract class DBcmd {
    protected List<Condition> conditions;
    protected List<String> columnNames;
    protected List<String> tableNames;
    protected String DBname;
    protected String commandType;

    public DBcmd() {
    }

    public DBcmd(List<Condition> conditions, List<String> columnNames, List<String> tableNames, String DBname, String commandType) {
        this.conditions = conditions;
        this.columnNames = columnNames;
        this.tableNames = tableNames;
        this.DBname = DBname;
        this.commandType = commandType;
    }

    /**
     * Potentially mutates database server state
     * @return result of query
     */
    public abstract String query(DBServer s);

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
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

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }
}
