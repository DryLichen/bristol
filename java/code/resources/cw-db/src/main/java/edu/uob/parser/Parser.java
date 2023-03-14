package edu.uob.parser;

import java.util.ArrayList;

public class Parser {
    private String command;

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public ArrayList<String> getTokens() {
        // check the terminator ;
        if (!command.endsWith(";")) {
            System.out.println("lack terminator ;");
            return null;
        }

        // delete the terminator,
        String[] tokens = command.split(" ");

    }
}
