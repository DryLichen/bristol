package edu.uob.parser;

import edu.uob.comman.Utils;
import edu.uob.exception.DBException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * some ideas are from java lecture
 * get tokens from command
 */
public class Tokenizer {
    private String query;
    private ArrayList<String> specialCharacters = new ArrayList<>();
    private ArrayList<Token> tokenList = new ArrayList<>();

    public Tokenizer(String query) {
        this.query = query;
    }

    /**
     * initiate the list of tokens
     */
    public void setup() throws DBException {
        ArrayList<String> tokens = new ArrayList<>();

        // Remove any whitespace at the beginning and end of the query
        query = query.trim();
        // Split the query on single quotes (to separate out query characters from string literals)
        String[] fragments = query.split("'");

        for (int i = 0; i < fragments.length; i++) {
            // Every odd fragment is a string literal, so append it without any alterations
            if (i % 2 != 0) {
                tokens.add("'" + fragments[i] + "'");
            } else {
                // Tokenize the fragments
                String[] nextBatchOfTokens = getTokenStrings(fragments[i]);
                tokens.addAll(Arrays.asList(nextBatchOfTokens));
            }
        }

        tokenize(tokens);
    }

    /**
     * @return literal tokens
     */
    private String[] getTokenStrings(String input) {
        specialCharacters.addAll(Arrays.asList("(", ")", ",", ";"));
        specialCharacters.addAll(Arrays.asList("==", ">=", "<=", "!=", " LIKE "));

        // Add in some extra padding spaces around the "special characters"
        // to be sure that they are separated by AT LEAST one space
        for (int i = 0; i < specialCharacters.size() - 1; i++) {
            input = input.replace(specialCharacters.get(i), " " + specialCharacters.get(i) + " ");
        }
        // replace regular expression characters
        input = input.replaceAll("(?<![=!<>])=(?![=!<>])", " " + "=" + " ");
        input = input.replaceAll("<(?!=)", " " + "<" + " ");
        input = input.replaceAll(">(?!=)", " " + ">" + " ");

        // Remove all double spaces (the previous replacements may have added some)
        while (input.contains("  ")) {
            input = input.replaceAll("  ", " ");
        }
        input = input.trim();

        return input.split(" ");
    }

    /**
     * @return convert literal tokens into Token class instances list
     */
    private void tokenize(ArrayList<String> tokens) throws DBException {
        for (String token : tokens) {
            if (Token.commandTypes.stream().anyMatch(i -> i.equalsIgnoreCase(token))) {
                tokenList.add(new Token(TokenType.COMMAND_TYPE, token));
            } else if (Token.keyWords.stream().anyMatch(i -> i.equalsIgnoreCase(token))) {
                tokenList.add(new Token(TokenType.KEY_WORD, token));
            } else if (Token.operators.stream().anyMatch(i -> i.equalsIgnoreCase(token))) {
                tokenList.add(new Token(TokenType.OPERATOR, token));
            } else if (Token.comparators.stream().anyMatch(i -> i.equalsIgnoreCase(token))) {
                tokenList.add(new Token(TokenType.COMPARATOR, token));
            } else if (Token.booleans.stream().anyMatch(i -> i.equalsIgnoreCase(token))) {
                tokenList.add(new Token(TokenType.BOOLEAN, token));
            } else if (Token.specialChars.stream().anyMatch(i -> i.equalsIgnoreCase(token))) {
                tokenList.add(new Token(TokenType.SPECIAL_CHAR, token));
            } else if ("NULL".equalsIgnoreCase(token)) {
                tokenList.add(new Token(TokenType.NULL, token));
            } else if (token.startsWith("'") && token.endsWith("'")) {
                tokenList.add(new Token(TokenType.STRING, token));
            }else if (checkTableAttribute(token)) {
                tokenList.add(new Token(TokenType.TABLE_ATTRIBUTE, token));
            } else if (checkWildAttribute(token)) {
                tokenList.add(new Token(TokenType.WILD_ATTRIBUTE, token));
            } else if (checkIdentifier(token)) {
                tokenList.add(new Token(TokenType.IDENTIFIER, token));
            } else if (Utils.checkInt(token)) {
                tokenList.add(new Token(TokenType.INTEGER, token));
            } else if (Utils.checkFloat(token)) {
                tokenList.add(new Token(TokenType.FLOAT, token));
            } else {
                throw new DBException("[ERROR]: Invalid token: " + token);
            }
        }
    }

    private boolean checkIdentifier(String token) {
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if (!isLetter(c) && !Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

    private boolean checkTableAttribute(String token) {
        // there should be only one full stop
        String[] subStrings = token.split("\\.");
        if (subStrings.length != 2) {
            return false;
        }

        for (String str : subStrings) {
             if (!checkIdentifier(str)) {
                 return false;
             }
        }

        return true;
    }

    private boolean checkWildAttribute(String token) {
        if ("*".equals(token)) {
            return true;
        }

        return false;
    }

    private boolean isLetter(char c) {
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c >='A' && c <= 'Z') {
            return true;
        }
        return false;
    }

    public ArrayList<Token> getTokenList() {
        return tokenList;
    }
}
