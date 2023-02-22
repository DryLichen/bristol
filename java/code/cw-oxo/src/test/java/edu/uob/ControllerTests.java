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

    void setupMore(int winThreshold) {
        model = new OXOModel(4, 6, winThreshold);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
    }

    // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
    void sendCommandToController(String command) {
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
        model.addPlayer(new OXOPlayer('A'));
        model.addPlayer(new OXOPlayer('B'));
        int numberOfPlayers = model.getNumberOfPlayers();
        String failComment = "The number of the players should be 4";
        assertEquals(4, numberOfPlayers, failComment);

        // play a round of game
        OXOPlayer firstPlayer = model.getPlayerByNumber(0);
        String command;
        for (int i = 0; i < model.getNumberOfRows(); i++) {
            for (int j = 0; j < model.getNumberOfColumns(); j++) {
                command = Character.toString('a' + i) + (j + 1);
                sendCommandToController(command);
            }
        }
        OXOPlayer winner = model.getWinner();
        failComment = "Fail to play a multi-player game";
        assertEquals(firstPlayer, winner, failComment);
    }

    @Test
    void testDraw() {
        setupMore(3);
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
        setupMore(3);
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        failComment = "Fail to detect horizontal win";
        sendCommandToController("A1");
        sendCommandToController("A2");
        sendCommandToController("a3");
        sendCommandToController("B1");
        sendCommandToController("a4");
        sendCommandToController("b2");
        sendCommandToController("a5");
        OXOPlayer winner = model.getWinner();
        assertEquals(firstPlayer, winner, failComment);

        // win vertically
        setupMore(3);
        firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        failComment = "Fail to detect vertical win";
        sendCommandToController("a1");
        sendCommandToController("a2");
        sendCommandToController("A3");
        sendCommandToController("a4");
        sendCommandToController("B2");
        sendCommandToController("b1");
        sendCommandToController("C2");
        sendCommandToController("c4");
        sendCommandToController("d2");
        winner = model.getWinner();
        assertEquals(firstPlayer, winner, failComment);

        // win diagonally
        setupMore(3);
        firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        failComment = "Fail to detect diagonal win";
        sendCommandToController("A1");
        sendCommandToController("a2");
        sendCommandToController("b1");
        sendCommandToController("B2");
        sendCommandToController("a3");
        sendCommandToController("A4");
        sendCommandToController("b4");
        sendCommandToController("B3");
        sendCommandToController("c5");
        winner = model.getWinner();
        assertEquals(firstPlayer, winner, failComment);

        setupMore(3);
        failComment = "Fail to detect not win";
        sendCommandToController("a1");
        sendCommandToController("a2");
        sendCommandToController("b2");
        sendCommandToController("c3");
        sendCommandToController("d4");
        winner = model.getWinner();
        assertEquals(null, winner, failComment);
    }

    @Test
    void afterWin() {
        setup();
        // can't claim any cell after win
        OXOPlayer firstPlayer = model.getPlayerByNumber(0);
        sendCommandToController("a1");
        sendCommandToController("a2");
        sendCommandToController("a3");
        sendCommandToController("b1");
        sendCommandToController("b2");
        sendCommandToController("b3");
        sendCommandToController("c1");
        sendCommandToController("c1");
        OXOPlayer winner = model.getWinner();
        String failComment = "None of the cells can be claimed after win";
        assertEquals(firstPlayer, winner, failComment);
        sendCommandToController("c3");
        assertEquals(null, model.getCellOwner(2, 2), failComment);
    }

    @Test
    void testWinThreshold() {
        setup();

        // decrease win threshold
        controller.decreaseWinThreshold();
        int winThreshold = model.getWinThreshold();
        String failComment = "Fail to decrease win threshold at the beginning of game";
        assertEquals(3, winThreshold, failComment);

        // increase win threshold
        controller.increaseWinThreshold();
        controller.increaseWinThreshold();
        winThreshold = model.getWinThreshold();
        failComment = "c";
        assertEquals(5, winThreshold, failComment);

        // can't decrease win threshold once the game is started
        sendCommandToController("a1");
        controller.decreaseWinThreshold();
        winThreshold = model.getWinThreshold();
        failComment = "Fail to stop decreasing win threshold after game starting";
        assertEquals(5, winThreshold,failComment);
        controller.increaseWinThreshold();
        winThreshold = model.getWinThreshold();
        failComment = "Fail to increase win threshold after game starting";
        assertEquals(6, winThreshold, failComment);

        // the minimum of win threshold is 3
        setupMore(4);
        controller.decreaseWinThreshold();
        winThreshold = model.getWinThreshold();
        failComment = "Fail to decrease the win threshold";
        assertEquals(3, winThreshold, failComment);

        controller.decreaseWinThreshold();
        winThreshold = model.getWinThreshold();
        failComment = "Fail to control the win threshold";
        assertEquals(3, winThreshold, failComment);
    }

    @Test
    void testReset() {
        setup();

        // fill the game board
        String command;
        for (int i = 0; i < model.getNumberOfRows(); i++) {
            for (int j = 0; j < model.getNumberOfColumns(); j++) {
                command = Character.toString('a' + i) + (j + 1);
                sendCommandToController(command);
            }
        }

        controller.reset();
        String failComment = "Fail to clear the game board";
        for (int i = 0; i < model.getNumberOfRows(); i++) {
            for (int j = 0; j < model.getNumberOfColumns(); j++) {
                assertEquals(null, model.getCellOwner(i, j), failComment);
            }
        }
    }

}
