package edu.uob.comman;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;
import edu.uob.parser.Token;
import edu.uob.parser.TokenType;

import java.io.File;
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

    public static boolean equalTokenType(TokenType tokenType, Token token) {
        return token.getTokenType().equals(tokenType);
    }

    public static boolean equalTokenValue(String value, Token token) {
        return token.getTokenValue().equalsIgnoreCase(value);
    }

    /**
     * @return result of == and != comparison
     */
    public static boolean isEqual(String expect, String value) {
        // null
        if ("NULL".equalsIgnoreCase(expect)) {
            if ("NULL".equalsIgnoreCase(value)) {
                return true;
            }
            return false;
        }

        // boolean
        if (Token.booleans.stream().anyMatch(i -> i.equalsIgnoreCase(expect))) {
            if (expect.equalsIgnoreCase(value)) {
                return true;
            }
            return false;
        }

        // integer and float
        if (checkFloat(expect)) {
            if (checkFloat(value)) {
                return Float.valueOf(expect).equals(Float.valueOf(value));
            }
            return false;
        }

        // general string
        return expect.equals(value);
    }

    /**
     * return 1 number if value is larger than expect value
     * -1 number if value is smaller than expect value
     * return 0 if they are equal
     * return -2 if the values can not be compared in this way
     */
    public static int compareValue(String expect, String value) {
        // null and boolean
        if ("NULL".equalsIgnoreCase(expect) ||
                Token.booleans.stream().anyMatch(i -> i.equalsIgnoreCase(expect))) {
            return -2;
        }

        // integer and float
        if (checkFloat(expect)) {
            if (checkFloat(value)) {
                int result = Float.valueOf(value).compareTo(Float.valueOf(expect));
                if (result > 0) {
                    return 1;
                } else if (result < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
            return -2;
        }

        // general string
        int result = value.compareTo(expect);
        if (result > 0) {
            return 1;
        } else if (result < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * only for string
     * @return true if the expect string is a substring of the value string
     */
    public static boolean isLike(String expect, String value) {
        // null, boolean, float, integer
        if ("NULL".equalsIgnoreCase(expect) ||
                Token.booleans.stream().anyMatch(i -> i.equalsIgnoreCase(expect)) ||
                checkFloat(expect)) {
            return false;
        }

        return value.contains(expect);
    }

    public static File getTableFile(DBServer s, FileIO fileIO, String tableName) throws DBException {
        String root = s.getStorageFolderPath();
        String specifiedDb = s.getSpecifiedDb();
        Assert.notNull(specifiedDb, Response.DB_NOT_SPECIFIED);
        File tableFile = fileIO.getTable(root, specifiedDb, tableName);
        Assert.fileExists(tableFile, Response.TABLE_NOT_EXIST);

        return tableFile;
    }

}
