package edu.uob.parser;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * some ideas are from java lecture
 * get tokens from command
 */
public class Tokenizer {
    private String query;
    private ArrayList<String> specialCharacters;
    private ArrayList<String> tokens;

    public Tokenizer() {
        specialCharacters = new ArrayList<>();
        specialCharacters.addAll(Arrays.asList("(", ")", ",", ";"));
        specialCharacters.addAll(Arrays.asList("==", ">=", "<=", "!=", " LIKE "));
    }

    public void setup() {
        // Remove any whitespace at the beginning and end of the query
        query = query.trim();

        // check the terminator ;
        if (!query.endsWith(";")) {
            System.out.println("lack terminator ;");
            return;
        }

        // Split the query on single quotes (to separate out query characters from string literals)
        String[] fragments = query.split("\'");
        // check the number of '
        if (fragments.length % 2 == 0) {
            System.out.println("invalid use of single-quote");
            return;
        }

        for (int i = 0; i < fragments.length; i++) {
            // Every odd fragment is a string literal, so append it without any alterations
            if (i % 2 != 0) {
                tokens.add("\'" + fragments[i] + "\'");
            } else {
                // Tokenize the fragments
                String[] nextBatchOfTokens = tokenize(fragments[i]);
                tokens.addAll(Arrays.asList(nextBatchOfTokens));
            }
        }
    }

    private String[] tokenize(String input) {
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

    public Token nextToken() {

    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<String> getSpecialCharacters() {
        return specialCharacters;
    }

    public void setSpecialCharacters(ArrayList<String> specialCharacters) {
        this.specialCharacters = specialCharacters;
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public void setTokens(ArrayList<String> tokens) {
        this.tokens = tokens;
    }

}
