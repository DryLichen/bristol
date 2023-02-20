package edu.uob;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTests {
    private OXOModel model;
    private OXOController controller;

    void setup() {
        model = new OXOModel(3, 3, 3);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
    }

    void setupMore() {
        model = new OXOModel(4, 6, 3);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
    }

    // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
    void sendCommandToController(String command) {
        // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
        // Note: this is ugly code and includes syntax that you haven't encountered yet
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
    }

    @Test
    void testDynamic() {
        setup();
        // enlarge the board to 5*4
        controller.addRow();
        controller.addColumn();
        controller.addRow();
        String failTestComment = "The number of the rows should be 5";
        int numberOfRows = model.getNumberOfRows();
        int numberOfColumns = model.getNumberOfColumns();
        assertEquals(5, numberOfRows, failTestComment);
        failTestComment = "The number of the columns should be 4";
        assertEquals(4, numberOfColumns, failTestComment);

        // enlarge the board to 4*5
        controller.removeColumn();
        controller.removeRow();
        controller.removeRow();
        controller.addColumn();;
        controller.addColumn();
        controller.addRow();
        failTestComment = "The number of the rows should be 4";
        numberOfRows = model.getNumberOfRows();
        assertEquals(4, numberOfRows, failTestComment);
        failTestComment = "The number of the columns should be 5";
        numberOfColumns = model.getNumberOfColumns();
        assertEquals(5, numberOfColumns, failTestComment);
    }

    @Test
    void testMultiPlayer() {
        setup();
        controller.addPlayer(new OXOPlayer('A'));
        controller.addPlayer(new OXOPlayer('B'));
        int numberOfPlayers = model.getNumberOfPlayers();
        String failTestComment = "The number of the players should be 4";
        assertEquals(4, numberOfPlayers, failTestComment);
    }

    @Test
    void testDraw() {
        setupMore();
        String command, failComment;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                if (i == 0 || i == 3) {
                    command = Character.toString('a' + i) + (j + 1);
                } else {
                    command = Character.toString('a' + i) + (6 - j);
                }

                sendCommandToController(command);
            }
        }

        failComment = "Fail to detect draw";
        boolean gameDrawn = model.isGameDrawn();
        assertTrue(gameDrawn, failComment);
    }

    @Test
    void testWin() {
        String failComment;

        // win horizontally
        setupMore();
        failComment = "Fail to detect horizontal win";
        sendCommandToController("");


        // win vertically
        setupMore();
        failComment = "Fail to detect vertical win";


        // win diagonally
        setupMore();
        failComment = "Fail to detect diagonal win";

    }

    @Test
    void testWinThreshold() {

    }

    @Test
    void testReset() {

    }

    @Test
    void testCommand() {

    }

    @Test
    void testAll() {

    }
}
