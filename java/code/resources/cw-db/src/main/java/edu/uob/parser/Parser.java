package edu.uob.parser;

import edu.uob.command.DBcmd;

import java.util.ArrayList;

public class Parser {
    private String command;
    private DBcmd dBcmd;

    public ArrayList<String> getTokens() {
        Tokenizer tokenizer = new Tokenizer();
        tokenizer.setup();
        return tokenizer.getTokens();
    }

    public DBcmd parse() {

        return null;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
