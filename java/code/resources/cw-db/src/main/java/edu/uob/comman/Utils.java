package edu.uob.comman;

import edu.uob.exception.DBException;
import edu.uob.exception.Response;
import edu.uob.parser.Token;
import edu.uob.parser.TokenType;

import java.util.Arrays;
import java.util.HashSet;

public class Utils {

    public static boolean checkInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean checkFloat(String string) {
        try {
            Float.parseFloat(string);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean isValue(Token token) {
        HashSet<TokenType> values = new HashSet<>();
        values.addAll(Arrays.asList(TokenType.BOOLEAN, TokenType.FLOAT, TokenType.INTEGER,
                TokenType.NULL, TokenType.STRING));
        return values.contains(token.getTokenType()) ||
                checkInt(token.getTokenValue()) || checkFloat(token.getTokenValue());
    }

    public static boolean isAttribute(Token token) {
        return TokenType.IDENTIFIER.equals(token.getTokenType()) ||
                TokenType.TABLE_ATTRIBUTE.equals(token.getTokenType());
    }
}
