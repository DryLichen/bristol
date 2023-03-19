package edu.uob.exception;

import edu.uob.comman.Utils;
import edu.uob.parser.Token;
import edu.uob.parser.TokenType;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

public class Assert {
    public static void equalValue(String expect, Token token, Response response) throws DBException {
        if (!expect.equalsIgnoreCase(token.getTokenValue())) {
            throw new DBException(response);
        }
    }

    public static void equalType(TokenType tokenType, Token token, Response response) throws DBException {
        if (!tokenType.equals(token.getTokenType())) {
            throw new DBException(response);
        }
    }

    public static void isAttribute(Token token) throws DBException {
        if (!TokenType.IDENTIFIER.equals(token.getTokenType()) &&
                !TokenType.TABLE_ATTRIBUTE.equals(token.getTokenType())) {
            throw new DBException(Response.NOT_ATTRIBUTE);
        }
    }

    public static void correctParamNum(int expectedNumber, int number) throws DBException {
        if (expectedNumber != number) {
            throw new DBException(Response.WRONG_PARAMETER_NUM.getMessage() + number);
        }
    }

    public static void isAlterationType(Token token) throws DBException {
        if (!"ADD".equalsIgnoreCase(token.getTokenValue()) &&
            !"DROP".equalsIgnoreCase(token.getTokenValue())) {
            throw new DBException(Response.NOT_ALTERATION);
        }
    }

    public static void isValue(Token token) throws DBException {
        HashSet<TokenType> values = new HashSet<>();
        values.addAll(Arrays.asList(TokenType.BOOLEAN, TokenType.FLOAT, TokenType.INTEGER,
                TokenType.NULL, TokenType.STRING));
        if (!values.contains(token.getTokenType()) &&
                !Utils.checkInt(token.getTokenValue()) &&
                !Utils.checkFloat(token.getTokenValue())) {
            throw new DBException(Response.NOT_VALUE);
        }
    }

    public static void fileExists(File fileName, Response response) throws DBException {
        if (!fileName.exists()) {
            throw new DBException(response);
        }
    }

    public static void notNull(Object object, Response response) throws DBException {
        if (object == null) {
            throw new DBException(response);
        }
    }

    public static void isTrue(Boolean bool, Response response) throws DBException {
        if (!bool) {
            throw new DBException(response);
        }
    }
}
