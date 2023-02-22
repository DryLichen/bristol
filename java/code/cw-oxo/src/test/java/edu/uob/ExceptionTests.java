package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class ExceptionTests {
    private OXOModel model;
    private OXOController controller;

    @BeforeEach
    void setup() {
        model = new OXOModel(3, 3, 3);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
    }

    void sendCommandToController(String command) {
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000),
                ()-> controller.handleIncomingCommand(command), timeoutComment);
    }

    @Test
    void testInvalidIdentifierLength() {
        String failComment = "Fail to throw invalid length exception";
        assertThrows(OXOMoveException.InvalidIdentifierLengthException.class,
                () -> controller.handleIncomingCommand("aaa"), failComment);
        assertThrows(OXOMoveException.InvalidIdentifierLengthException.class,
                () -> controller.handleIncomingCommand("a"), failComment);
        assertThrows(OXOMoveException.InvalidIdentifierLengthException.class,
                () -> controller.handleIncomingCommand("132"), failComment);
        assertThrows(OXOMoveException.InvalidIdentifierLengthException.class,
                () -> controller.handleIncomingCommand("a10"), failComment);
        assertThrows(OXOMoveException.InvalidIdentifierLengthException.class,
                () -> controller.handleIncomingCommand("`--+("), failComment);
        assertThrows(OXOMoveException.InvalidIdentifierLengthException.class,
                () -> controller.handleIncomingCommand(""), failComment);
    }

    @Test
    void testInvalidIdentifierCharacter() {
        String failComment = "Fail to throw invalid character exception";
        assertThrows(OXOMoveException.InvalidIdentifierCharacterException.class,
                () -> controller.handleIncomingCommand("aa"), failComment);
        assertThrows(OXOMoveException.InvalidIdentifierCharacterException.class,
                () -> controller.handleIncomingCommand("22"), failComment);
        assertThrows(OXOMoveException.InvalidIdentifierCharacterException.class,
                () -> controller.handleIncomingCommand("~-"), failComment);
        assertThrows(OXOMoveException.InvalidIdentifierCharacterException.class,
                () -> controller.handleIncomingCommand("9i"), failComment);
    }

    @Test
    void testOutsideCellRange() {
        String failComment = "Fail to throw out of range exception";
        assertThrows(OXOMoveException.OutsideCellRangeException.class,
                () -> controller.handleIncomingCommand("A0"), failComment);
        assertThrows(OXOMoveException.OutsideCellRangeException.class,
                () -> controller.handleIncomingCommand("A4"), failComment);
        assertThrows(OXOMoveException.OutsideCellRangeException.class,
                () -> controller.handleIncomingCommand("d2"), failComment);
        assertThrows(OXOMoveException.OutsideCellRangeException.class,
                () -> controller.handleIncomingCommand("i9"), failComment);
    }

    @Test
    void testCellAlreadyTaken() {
        String failComment = "Fail to throw cell already taken exception";
        sendCommandToController("a1");
        sendCommandToController("c3");
        assertThrows(OXOMoveException.CellAlreadyTakenException.class,
                () -> controller.handleIncomingCommand("a1"), failComment);
        assertThrows(OXOMoveException.CellAlreadyTakenException.class,
                () -> controller.handleIncomingCommand("c3"), failComment);
    }
}
