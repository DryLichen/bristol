package edu.uob;

import edu.uob.exception.DBException;
import edu.uob.parser.Parser;
import edu.uob.parser.Token;
import edu.uob.parser.TokenType;
import edu.uob.parser.Tokenizer;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

/**
 * tests for p
 */
public class TokenizerTests {

    @Test
    public void testTokenizer() {
        String message = "Token is not the same with expect token";
        String command = "CReATE taBle table1  (name, EMAIL) * (table1.name, 'simon'," +
                "+24, -24.3, nuLL, falSe) aNd >= = !=  LIKE  ; ";
        Tokenizer tokenizer = new Tokenizer(command);
        try {
            tokenizer.setup();
        } catch (DBException e) {
            System.out.println("Fail to setup tokenizer");
        }
        ArrayList<Token> tokens = tokenizer.getTokenList();

        ArrayList<Token> expectTokens = new ArrayList<>();
        expectTokens.add(new Token(TokenType.COMMAND_TYPE, "CReATE"));
        expectTokens.add(new Token(TokenType.KEY_WORD, "taBle"));
        expectTokens.add(new Token(TokenType.IDENTIFIER, "table1"));
        expectTokens.add(new Token(TokenType.SPECIAL_CHAR, "("));
        expectTokens.add(new Token(TokenType.IDENTIFIER, "name"));
        expectTokens.add(new Token(TokenType.SPECIAL_CHAR, ","));
        expectTokens.add(new Token(TokenType.IDENTIFIER, "EMAIL"));
        expectTokens.add(new Token(TokenType.SPECIAL_CHAR, ")"));
        expectTokens.add(new Token(TokenType.WILD_ATTRIBUTE, "*"));
        expectTokens.add(new Token(TokenType.SPECIAL_CHAR, "("));
        expectTokens.add(new Token(TokenType.TABLE_ATTRIBUTE, "table1.name"));
        expectTokens.add(new Token(TokenType.SPECIAL_CHAR, ","));
        expectTokens.add(new Token(TokenType.STRING, "'simon'"));
        expectTokens.add(new Token(TokenType.SPECIAL_CHAR, ","));
        expectTokens.add(new Token(TokenType.INTEGER, "+24"));
        expectTokens.add(new Token(TokenType.SPECIAL_CHAR, ","));
        expectTokens.add(new Token(TokenType.FLOAT, "-24.3"));
        expectTokens.add(new Token(TokenType.SPECIAL_CHAR, ","));
        expectTokens.add(new Token(TokenType.NULL, "nuLL"));
        expectTokens.add(new Token(TokenType.SPECIAL_CHAR, ","));
        expectTokens.add(new Token(TokenType.BOOLEAN, "falSe"));
        expectTokens.add(new Token(TokenType.SPECIAL_CHAR, ")"));
        expectTokens.add(new Token(TokenType.OPERATOR, "aNd"));
        expectTokens.add(new Token(TokenType.COMPARATOR, ">="));
        expectTokens.add(new Token(TokenType.KEY_WORD, "="));
        expectTokens.add(new Token(TokenType.COMPARATOR, "!="));
        expectTokens.add(new Token(TokenType.COMPARATOR, "LIKE"));
        expectTokens.add(new Token(TokenType.SPECIAL_CHAR, ";"));

        for (int i = 0; i < expectTokens.size(); i++) {
            assertEquals(expectTokens.get(i).getTokenType(), tokens.get(i).getTokenType(), message);
            assertEquals(expectTokens.get(i).getTokenValue(), tokens.get(i).getTokenValue(), message);
        }
    }

}
