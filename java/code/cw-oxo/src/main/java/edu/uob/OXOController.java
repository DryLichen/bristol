package edu.uob;

import edu.uob.OXOMoveException.*;

import java.util.HashMap;

public class OXOController {
    OXOModel gameModel;
    private boolean isStarted = false;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    /**
     * interpret input command
     * @param command input from keyboard
     * @throws OXOMoveException
     */
    public void handleIncomingCommand(String command) throws OXOMoveException {
        // only execute the command when the game isn't end
        if (gameModel.getWinner() != null || checkDraw()) {
            return;
        }

        if (command.length() == 2) {
            // get the location of target cell
            int rowNum = getRowNum(command.charAt(0));
            int columnNum = getColumnNum(command.charAt(1));

            if (rowNum >= gameModel.getNumberOfRows() || rowNum < 0) {
                throw new OXOMoveException.OutsideCellRangeException(RowOrColumn.ROW, rowNum);
            } else if (columnNum >= gameModel.getNumberOfColumns() ||  columnNum < 0) {
                throw new OXOMoveException.OutsideCellRangeException(RowOrColumn.COLUMN, columnNum);
            } else if (gameModel.getCellOwner(rowNum, columnNum) != null) {
                throw new OXOMoveException.CellAlreadyTakenException(rowNum, columnNum);
            } else {
                OXOPlayer currPlayer = gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber());
                gameModel.setCellOwner(rowNum, columnNum, currPlayer);
                isStarted = true;
                checkWin();
                checkDraw();
                gameModel.setCurrentPlayerNumber(getNextPlayer());
            }
        } else {
            throw new OXOMoveException.InvalidIdentifierLengthException(command.length());
        }
    }

    /**
     * @return the row number of target cell
     */
    private int getRowNum(Character rowChar) throws InvalidIdentifierCharacterException {
        int rowNum;
        if (!Character.isAlphabetic(rowChar)) {
            throw new OXOMoveException.InvalidIdentifierCharacterException(RowOrColumn.ROW, rowChar);
        } else {
            if (Character.isUpperCase(rowChar)) {
                rowNum = rowChar - 'A';
            } else {
                rowNum = rowChar - 'a';
            }
        }

        return rowNum;
    }

    /**
     * @return the column number of target cell
     */
    private int getColumnNum(Character columnChar) throws InvalidIdentifierCharacterException {
        int columnNum;
        if (!Character.isDigit(columnChar)) {
            throw new OXOMoveException.InvalidIdentifierCharacterException(RowOrColumn.COLUMN, columnChar);
        } else {
            columnNum = columnChar - '1';
        }

        return columnNum;
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
        if (gameModel.getNumberOfRows() >= 9) {
            return;
        }
        gameModel.addRow();
        checkWin();
        checkDraw();
    }

    /**
     * decrease the number of rows by one
     */
    public void removeRow() {
        if (gameModel.getNumberOfRows() <= 1) {
            return;
        }
        gameModel.removeRow();
        checkWin();
        checkDraw();
    }

    /**
     * increase the number of columns by one
     */
    public void addColumn() {
        if (gameModel.getNumberOfColumns() >= 9) {
            return;
        }
        gameModel.addColumn();
        checkWin();
        checkDraw();
    }

    /**
     * decrease the number of columns by one
     */
    public void removeColumn() {
        if (gameModel.getNumberOfColumns() <= 1) {
            return;
        }
        gameModel.removeColumn();
        checkWin();
        checkDraw();
    }

    public void increaseWinThreshold() {
        gameModel.setWinThreshold(gameModel.getWinThreshold() + 1);
    }

    public void decreaseWinThreshold() {
        if (gameModel.getWinThreshold() > 3 && !isStarted) {
            gameModel.setWinThreshold(gameModel.getWinThreshold() - 1);
        }
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

        isStarted = false;
        gameModel.setCurrentPlayerNumber(0);
        gameModel.setWinner(null);
        gameModel.cancelGameDrawn();
    }

    /**
     * @return the Letter of the winner; null for no winner
     */
    private Character checkWin() {
        // win horizontally
        Character winHorizontal = winHorizontal();
        if (winHorizontal != null) {
            gameModel.setWinner(gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber()));
            return winHorizontal;
        }

        // win vertically
        Character winVertical = winVertical();
        if (winVertical != null) {
            gameModel.setWinner(gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber()));
            return winVertical;
        }

        // win diagonally
        Character winDiagonal = winDiagonal();
        if (winDiagonal != null) {
            gameModel.setWinner(gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber()));
            return winDiagonal;
        }

        gameModel.setWinner(null);
        return null;
    }

    /**
     * @return true if there is a draw
     */
    private boolean checkDraw() {
        if (gameModel.getWinner() != null) {
            return false;
        }

        int count = 0;
        int numRows = gameModel.getNumberOfRows();
        int numColumns = gameModel.getNumberOfColumns();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                if (getCellLetter(i, j) != null) {
                    count++;
                }
            }
        }

        if (count == numRows * numColumns) {
            gameModel.setGameDrawn();
            return true;
        } else {
            gameModel.cancelGameDrawn();
        }
        return false;
    }

    /**
     * @return get the map of player's letter and frequency in board
     */
    private HashMap<Character, Integer> getPlayerMap() {
        int numberOfPlayers = gameModel.getNumberOfPlayers();
        HashMap<Character, Integer> playerMap = new HashMap<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            playerMap.put(gameModel.getPlayerByNumber(i).getPlayingLetter(), 0);
        }

        return playerMap;
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
        int winThreshold = gameModel.getWinThreshold();

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j <= numColumns - winThreshold; j++) {

                for (int k = 0; k < winThreshold; k++) {
                    for (Character letter : playerMap.keySet()) {
                        if (letter.equals(getCellLetter(i, j + k))) {
                            playerMap.replace(letter, playerMap.get(letter) + 1);
                        }
                    }
                }

                Character winnerLetter = getWinnerLetter(playerMap);
                if (winnerLetter != null) {
                    return winnerLetter;
                }

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
        int winThreshold = gameModel.getWinThreshold();

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j <= numRows - winThreshold; j++) {

                for (int k = 0; k < winThreshold; k++) {
                    for (Character letter : playerMap.keySet()) {
                        if (letter.equals(getCellLetter(j + k, i))) {
                            playerMap.replace(letter, playerMap.get(letter) + 1);
                        }
                    }
                }

                Character winnerLetter = getWinnerLetter(playerMap);
                if (winnerLetter != null) {
                    return winnerLetter;
                }

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

        // from left to right
        for (int i = 0; i <= numRows - winThreshold; i++) {
            for (int j = 0; j <= numColumns - winThreshold; j++) {
                for (int z = 0; z < winThreshold; z++) {
                    for (Character letter : playerMap.keySet()) {
                        if (letter.equals(getCellLetter(i + z, j + z))) {
                            playerMap.replace(letter, playerMap.get(letter) + 1);
                        }
                    }
                }

                Character winnerLetter = getWinnerLetter(playerMap);
                if (winnerLetter != null) {
                    return winnerLetter;
                }
            }
        }

        // from right to left
        for (int i = 0; i <= numRows - winThreshold; i++) {
            for (int j = numColumns - 1; j >= winThreshold - 1; j--) {
                for (int z = 0; z < winThreshold; z++) {
                    for (Character letter : playerMap.keySet()) {
                        if (letter.equals(getCellLetter(i + z, j - z))) {
                            playerMap.replace(letter, playerMap.get(letter) + 1);
                        }
                    }
                }

                Character winnerLetter = getWinnerLetter(playerMap);
                if (winnerLetter != null) {
                    return winnerLetter;
                }
            }
        }

        return null;
    }

    // return the letter of the owner of one cell
    private Character getCellLetter(int rowNum, int columnNum) {
        OXOPlayer cellOwner = gameModel.getCellOwner(rowNum, columnNum);
        if (cellOwner != null) {
            return cellOwner.getPlayingLetter();
        }

        return null;
    }

    /**
     * @param oxoPlayer add more players in the game
     */
    public void addPlayer(OXOPlayer oxoPlayer) {
        gameModel.addPlayer(oxoPlayer);
    }
}
