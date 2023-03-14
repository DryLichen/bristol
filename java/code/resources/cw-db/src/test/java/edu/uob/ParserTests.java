package edu.uob;

import edu.uob.parser.Tokenizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ParserTests {

    @Test
    public void testTokens() {
        Tokenizer tokenizer = new Tokenizer();
        tokenizer.setQuery("  INSERT  INTO  people  VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ; ");

        tokenizer.setup();
        ArrayList<String> tokens = tokenizer.getTokens();
        tokens.forEach((i) -> System.out.println(i));
    }

    @Test
    public void testParser() {

    }

}

