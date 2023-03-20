package edu.uob.parser;

import java.util.ArrayList;
import java.util.Arrays;

public class Token {
    private TokenType tokenType;
    private String tokenValue;

    public static ArrayList<String> commandTypes;
    public static ArrayList<String> keyWords;
    public static ArrayList<String> operators;
    public static ArrayList<String> comparators;
    public static ArrayList<String> booleans;
    public static ArrayList<String> specialChars;

    static {
        commandTypes = new ArrayList<>();
        commandTypes.addAll(Arrays.asList("USE", "CREATE", "DROP", "ALTER", "INSERT",
                "SELECT", "UPDATE", "DELETE", "JOIN"));

        keyWords = new ArrayList<>();
        keyWords.addAll(Arrays.asList("DATABASE", "TABLE", "INTO", "VALUES", "FROM",
                "SET", "WHERE", "ON", "ADD", "DROP", "="));

        comparators = new ArrayList<>();
        comparators.addAll(Arrays.asList("==", ">", "<", ">=", "<=", "!=", " LIKE "));

        operators = new ArrayList<>(Arrays.asList("AND", "OR"));

        booleans = new ArrayList<>(Arrays.asList("TRUE", "FALSE"));

        specialChars = new ArrayList<>();
        specialChars.addAll(Arrays.asList("(", ")", ",", ";"));
    }

    public Token() {
    }

    public Token(TokenType tokenType, String tokenValue) {
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}
