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

    /**
     * @return the number of next player
     */
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
        // the minimum of number of rows is 1
        if (gameModel.getNumberOfRows() <= 1) {
            return;
        }
        // a row can't be removed when there are claimed cells on it
        if (!isRowEmpty(gameModel.getNumberOfRows() - 1)) {
            return;
        }
        // a row can't be removed when this action leads to a draw
        if (leadToDraw(gameModel.getNumberOfRows() - 1, gameModel.getNumberOfColumns())) {
            return;
        }

        gameModel.removeRow();
        checkWin();
        checkDraw();
    }

    /**
     * @return if a row is empty, return true; otherwise, return false
     */
    private boolean isRowEmpty(int rowNum) {
        int numColumns = gameModel.getNumberOfColumns();
        for (int i = 0; i < numColumns; i++) {
            if (gameModel.getCellOwner(rowNum, i) != null) {
                return false;
            }
        }

        return true;
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
        // the minimum of number of columns is 1
        if (gameModel.getNumberOfColumns() <= 1) {
            return;
        }
        // a row can't be removed when there are claimed cells on it
        if (!isColumnEmpty(gameModel.getNumberOfColumns() - 1)) {
            return;
        }
        // a column can't be removed when this action leads to a draw
        if (leadToDraw(gameModel.getNumberOfRows(), gameModel.getNumberOfColumns() - 1)) {
            return;
        }

        gameModel.removeColumn();
        checkWin();
        checkDraw();
    }

    /**
     * @return if a column is empty, return true; otherwise, return false
     */
    private boolean isColumnEmpty(int columnNum) {
        int numRows = gameModel.getNumberOfRows();
        for (int i = 0; i < numRows; i++) {
            if (gameModel.getCellOwner(i, columnNum) != null) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return if deleting a row or a column leads to a draw, return true; otherwise return false
     */
    private boolean leadToDraw(int numRows, int numColumns) {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                if (gameModel.getCellOwner(i, j) == null) {
                    return false;
                }
            }
        }

        return true;
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
     * @return the winner; null for no winner
     */
    private OXOPlayer checkWin() {
        // win horizontally
        OXOPlayer winnerHorizontal = winHorizontal();
        if (winnerHorizontal != null) {
            gameModel.setWinner(winnerHorizontal);
            return winnerHorizontal;
        }

        // win vertically
        OXOPlayer winnerVertical = winVertical();
        if (winnerVertical != null) {
            gameModel.setWinner(winnerVertical);
            return winnerVertical;
        }

        // win diagonally
        OXOPlayer winnerDiagonal = winDiagonal();
        if (winnerDiagonal != null) {
            gameModel.setWinner(winnerDiagonal);
            return winnerDiagonal;
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
                if (gameModel.getCellOwner(i, j) != null) {
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
    private HashMap<OXOPlayer, Integer> getPlayerMap() {
        int numberOfPlayers = gameModel.getNumberOfPlayers();
        HashMap<OXOPlayer, Integer> playerMap = new HashMap<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            playerMap.put(gameModel.getPlayerByNumber(i), 0);
        }

        return playerMap;
    }

    /**
     * @return the winner; return null when the game is still on
     */
    private OXOPlayer getWinner(HashMap<OXOPlayer, Integer> playerMap) {
        for (OXOPlayer player : playerMap.keySet()) {
            if (playerMap.get(player).equals(gameModel.getWinThreshold())) {
                return player;
            } else {
                playerMap.replace(player, 0);
            }
        }
        return null;
    }

    /**
     * @return the winner; null for no winner
     */
    private OXOPlayer winHorizontal() {
        HashMap<OXOPlayer, Integer> playerMap = getPlayerMap();
        int numRows = gameModel.getNumberOfRows();
        int numColumns = gameModel.getNumberOfColumns();
        int winThreshold = gameModel.getWinThreshold();

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j <= numColumns - winThreshold; j++) {

                for (int k = 0; k < winThreshold; k++) {
                    for (OXOPlayer player : playerMap.keySet()) {
                        if (player.equals(gameModel.getCellOwner(i, j + k))) {
                            playerMap.replace(player, playerMap.get(player) + 1);
                        }
                    }
                }

                OXOPlayer winner = getWinner(playerMap);
                if (winner != null) {
                    return winner;
                }
            }
        }

        return null;
    }

    /**
     * @return the winner; null for no winner
     */
    private OXOPlayer winVertical() {
        HashMap<OXOPlayer, Integer> playerMap = getPlayerMap();
        int numRows = gameModel.getNumberOfRows();
        int numColumns = gameModel.getNumberOfColumns();
        int winThreshold = gameModel.getWinThreshold();

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j <= numRows - winThreshold; j++) {

                for (int k = 0; k < winThreshold; k++) {
                    for (OXOPlayer player : playerMap.keySet()) {
                        if (player.equals(gameModel.getCellOwner(j + k, i))) {
                            playerMap.replace(player, playerMap.get(player) + 1);
                        }
                    }
                }

                OXOPlayer winner = getWinner(playerMap);
                if (winner != null) {
                    return winner;
                }
            }
        }

        return null;
    }

    /**
     * @return the winner; null for no winner
     */
    private OXOPlayer winDiagonal() {
        HashMap<OXOPlayer, Integer> playerMap = getPlayerMap();
        int numRows = gameModel.getNumberOfRows();
        int numColumns = gameModel.getNumberOfColumns();
        int winThreshold = gameModel.getWinThreshold();

        // from left to right
        for (int i = 0; i <= numRows - winThreshold; i++) {
            for (int j = 0; j <= numColumns - winThreshold; j++) {
                for (int z = 0; z < winThreshold; z++) {
                    for (OXOPlayer player : playerMap.keySet()) {
                        if (player.equals(gameModel.getCellOwner(i + z, j + z))) {
                            playerMap.replace(player, playerMap.get(player) + 1);
                        }
                    }
                }

                OXOPlayer winner = getWinner(playerMap);
                if (winner != null) {
                    return winner;
                }
            }
        }

        // from right to left
        for (int i = 0; i <= numRows - winThreshold; i++) {
            for (int j = numColumns - 1; j >= winThreshold - 1; j--) {
                for (int z = 0; z < winThreshold; z++) {
                    for (OXOPlayer player : playerMap.keySet()) {
                        if (player.equals(gameModel.getCellOwner(i + z, j - z))) {
                            playerMap.replace(player, playerMap.get(player) + 1);
                        }
                    }
                }

                OXOPlayer winner = getWinner(playerMap);
                if (winner != null) {
                    return winner;
                }
            }
        }

        return null;
    }
}
