package edu.uob.command;

public abstract class DBcmd {
    List<Condition> conditions;
    List<String> colNames;
    List<String> tableNames;
    String DBname;
    String commandType;

    String query(DBServer s) {

    }
}
