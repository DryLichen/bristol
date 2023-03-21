package edu.uob.exception;

import edu.uob.comman.Utils;
import edu.uob.parser.Token;
import edu.uob.parser.TokenType;

import java.io.File;

public class Assert {
    public static void equalValue(String expect, Token token, Response response) throws DBException {
        if (!Utils.equalTokenValue(expect, token)) {
            throw new DBException(response);
        }
    }

    public static void equalType(TokenType tokenType, Token token, Response response) throws DBException {
        if (!Utils.equalTokenType(tokenType, token)) {
            throw new DBException(response);
        }
    }

    public static void isAttribute(Token token) throws DBException {
        if (!Utils.isAttribute(token)) {
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
        if (!Utils.isValue(token)) {
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
