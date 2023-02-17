package edu.uob;

import edu.uob.OXOMoveException.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    /**
     * interpret input command
     * @param command input from keyboard
     * @throws OXOMoveException
     */
    public void handleIncomingCommand(String command) throws OXOMoveException {
        int length = command.length();
        if (length == 2 && checkWin().equals(GAMESTATE.NOWINNER)) {
            // get the location of target cell
            char rowChar = command.charAt(0);
            char columnChar = command.charAt(1);
            int rowNum, columnNum;
            if (!Character.isAlphabetic(rowChar)) {
                throw new OXOMoveException.InvalidIdentifierCharacterException(RowOrColumn.ROW, rowChar);
            } else if (!Character.isDigit(columnChar)) {
                throw new OXOMoveException.InvalidIdentifierCharacterException(RowOrColumn.COLUMN, columnChar);
            } else {
                if (Character.isUpperCase(rowChar)) {
                    rowNum = rowChar - 'A';
                } else {
                    rowNum = rowChar - 'a';
                }
                columnNum = command.charAt(1) - '1';
            }

            if (rowNum >= gameModel.getNumberOfRows()) {
                throw new OXOMoveException.OutsideCellRangeException(RowOrColumn.ROW, rowNum);
            } else if (columnNum >= gameModel.getNumberOfColumns()) {
                throw new OXOMoveException.OutsideCellRangeException(RowOrColumn.COLUMN, columnNum);
            } else if (gameModel.getCellOwner(rowNum, columnNum) != null) {
                throw new OXOMoveException.CellAlreadyTakenException(rowNum, columnNum);
            } else {
                OXOPlayer currPlayer = gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber());
                gameModel.setCellOwner(rowNum, columnNum, currPlayer);
                GAMESTATE gameState = checkWin();
                if (gameState.equals(GAMESTATE.XWIN) || gameState.equals(GAMESTATE.OWIN)) {
                    gameModel.setWinner(currPlayer);
                } else if (gameState.equals(GAMESTATE.DRAW)) {
                    gameModel.setGameDrawn();
                }

                gameModel.setCurrentPlayerNumber(getNextPlayer());
            }
        } else {
            throw new OXOMoveException.InvalidIdentifierLengthException(length);
        }
    }

    private int getNextPlayer() {
        int numberOfPlayers = gameModel.getNumberOfPlayers();
        int playerNum = gameModel.getCurrentPlayerNumber();
        if (playerNum < numberOfPlayers - 1) {
            return playerNum + 1;
        } else {
            return 0;
        }
    }

    /**
     * increase the number of rows by one
     */
    public void addRow() {
        if (checkWin().equals(GAMESTATE.NOWINNER)) {
            gameModel.addRow();
        }
    }

    /**
     * decrease the number of rows by one
     */
    public void removeRow() {
        if (checkWin().equals(GAMESTATE.NOWINNER)) {
            gameModel.removeRow();
        }
    }

    /**
     * increase the number of columns by one
     */
    public void addColumn() {
        if (checkWin().equals(GAMESTATE.NOWINNER)) {
            gameModel.addColumn();
        }
    }

    /**
     * decrease the number of columns by one
     */
    public void removeColumn() {
        if (checkWin().equals(GAMESTATE.NOWINNER)) {
            gameModel.removeColumn();
        }
    }

    public void increaseWinThreshold() {
        gameModel.setWinThreshold(gameModel.getWinThreshold() + 1);
    }

    public void decreaseWinThreshold() {
        gameModel.setWinThreshold(gameModel.getWinThreshold() - 1);
    }

    /**
     * clear the game board
     */
    public void reset() {
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                gameModel.setCellOwner(i, j, null);
            }
        }
    }

    /**
     * @return the Letter of the winner; null for no winner
     */
    public Character checkWin() {
        // win horizontally
        Character winHorizontal = winHorizontal();
        if (winHorizontal != null) {
            return winHorizontal;
        }

        // win vertically
        Character winVertical = winVertical();
        if (winVertical != null) {
            return winVertical;
        }

        // win diagonally
        Character winDiagonal = winDiagonal();
        if (winDiagonal != null) {
            return winDiagonal;
        }

        return null;
    }

    /**
     * @return get the map of letter and frequency
     */
    private HashMap<Character, Integer> getPlayerMap() {
        int numberOfPlayers = gameModel.getNumberOfPlayers();
        HashMap<Character, Integer> players = new HashMap<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            players.put(gameModel.getPlayerByNumber(i).getPlayingLetter(), 0);
        }

        return players;
    }

    /**
     * @return the winner's letter; return null when the game is still on
     */
    private Character getWinnerLetter(HashMap<Character, Integer> playerMap) {
        for (Character letter : playerMap.keySet()) {
            if (playerMap.get(letter).equals(gameModel.getWinThreshold())) {
                return letter;
            } else {
                playerMap.replace(letter, 0);
            }
        }
        return null;
    }

    /**
     * @return the Letter of the winner; null for no winner
     */
    private Character winHorizontal() {
        HashMap<Character, Integer> playerMap = getPlayerMap();
        int numRows = gameModel.getNumberOfRows();
        int numColumns = gameModel.getNumberOfColumns();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                for (Character letter : playerMap.keySet()) {
                    if (letter.equals(getLetter(i, j))) {
                        playerMap.replace(letter, playerMap.get(letter) + 1);
                    }
                }
            }

            Character winnerLetter = getWinnerLetter(playerMap);
            if (winnerLetter != null) {
                return winnerLetter;
            }
        }

        return null;
    }

    /**
     * @return the Letter of the winner; null for no winner
     */
    private Character winVertical() {
        HashMap<Character, Integer> playerMap = getPlayerMap();
        int numRows = gameModel.getNumberOfRows();
        int numColumns = gameModel.getNumberOfColumns();
        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                for (Character letter : playerMap.keySet()) {
                    if (letter.equals(getLetter(i, j))) {
                        playerMap.replace(letter, playerMap.get(letter) + 1);
                    }
                }
            }

            Character winnerLetter = getWinnerLetter(playerMap);
            if (winnerLetter != null) {
                return winnerLetter;
            }
        }

        return null;
    }

    /**
     * @return the Letter of the winner; null for no winner
     */
    private Character winDiagonal() {
        HashMap<Character, Integer> playerMap = getPlayerMap();
        int numRows = gameModel.getNumberOfRows();
        int numColumns = gameModel.getNumberOfColumns();
        int winThreshold = gameModel.getWinThreshold();

        for (int i = 0; i < numRows - winThreshold; i++) {
            for (int j = 0; j < numColumns; j++) {

            }
        }

        for (int i = numRows - 1; i >= 0 ; i--) {
            if (X.equals(getLetter(i, i))) {
                xCount++;
            } else if (O.equals(getLetter(i, i))){
                oCount++;
            }
        }
        if (xCount == gameModel.getWinThreshold()) {
            return GAMESTATE.XWIN;
        } else if (oCount == gameModel.getWinThreshold()) {
            return GAMESTATE.OWIN;
        }

        return null;
    }

    /**
     * @return true if all the cells are filled by letters
     */
    private boolean isFull() {
        int count = 0;
        int numRows = gameModel.getNumberOfRows();
        int numColumns = gameModel.getNumberOfColumns();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                if (X.equals(getLetter(i, j)) || O.equals(getLetter(i, j))) {
                    count++;
                }
            }
        }

        return count == numRows * numColumns;
    }

    // return the letter of the owner of one cell
    private Character getLetter(int rowNum, int columnNum) {
        OXOPlayer cellOwner = gameModel.getCellOwner(rowNum, columnNum);
        if (cellOwner != null) {
            return cellOwner.getPlayingLetter();
        }

        return null;
    }
}
