package edu.uob;

import edu.uob.OXOMoveException.*;

public class OXOController {
    OXOModel gameModel;
    private final Character X = 'X';
    private final Character O = 'O';

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
        if (length == 2 && checkWin() == 0) {
            // get the location of target cell
            char rowChar = command.charAt(0);
            int rowNum;
            if (Character.isUpperCase(rowChar)) {
                rowNum = rowChar - 'A';
            } else {
                rowNum = rowChar - 'a';
            }
            int columnNum = command.charAt(1) - '1';

            OXOPlayer currPlayer = gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber());
            gameModel.setCellOwner(rowNum, columnNum, currPlayer);
            int gameState = checkWin();
            if (gameState == 1 || gameState == 2) {
                gameModel.setWinner(currPlayer);
            } else if (gameState == 3) {
                gameModel.setGameDrawn();
            }

            gameModel.setCurrentPlayerNumber(getNextPlayer());
        } else if (command.equals("esc")) {
            reset();
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
        if (checkWin() == 0) {
            gameModel.addRow();
        }
    }

    /**
     * decrease the number of rows by one
     */
    public void removeRow() {
        if (checkWin() == 0) {
            gameModel.removeRow();
        }
    }

    /**
     * increase the number of columns by one
     */
    public void addColumn() {
        if (checkWin() == 0) {
            gameModel.addColumn();
        }
    }

    /**
     * decrease the number of columns by one
     */
    public void removeColumn() {
        if (checkWin() == 0) {
            gameModel.removeColumn();
        }
    }

    public void increaseWinThreshold() {

    }

    public void decreaseWinThreshold() {

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
     * @return 0 for no winner; 1 for x win; 2 for o win; 3 for draw
     */
    public int checkWin() {
        // win horizontally
        int winHorizontal = winHorizontal();
        if (winHorizontal != 0) {
            return winHorizontal;
        }

        // win vertically
        int winVertical = winVertical();
        if (winVertical != 0) {
            return winVertical;
        }

        // win diagonally
        int winDiagonal = winDiagonal();
        if (winDiagonal != 0) {
            return winDiagonal;
        }

        // check if there is a draw
        if (isFull()) {
            return 3;
        }

        return 0;
    }

    /**
     * @return 0 for no winner; 1 for x win; 2 for o win
     */
    private int winHorizontal() {
        int xCount = 0, oCount = 0;
        int numRows = gameModel.getNumberOfRows();
        int numColumns = gameModel.getNumberOfColumns();

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                if (X.equals(getLetter(i, j))) {
                    xCount++;
                } else if (O.equals(getLetter(i, j))) {
                    oCount++;
                }
            }

            if (xCount == numColumns) {
                return 1;
            } else if (oCount == numColumns) {
                return 2;
            } else {
                xCount = oCount = 0;
            }
        }

        return 0;
    }

    /**
     * @return 0 for no winner; 1 for x win; 2 for o win
     */
    private int winVertical() {
        int xCount = 0, oCount = 0;
        int numRows = gameModel.getNumberOfRows();
        int numColumns = gameModel.getNumberOfColumns();

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                if (X.equals(getLetter(i, j))) {
                    xCount++;
                } else if (O.equals(getLetter(i, j))) {
                    oCount++;
                }
            }

            if (xCount == numRows) {
                return 1;
            } else if (oCount == numRows) {
                return 2;
            } else {
                xCount = oCount = 0;
            }
        }

        return 0;
    }

    /**
     * @return 0 for no winner; 1 for x win; 2 for o win
     */
    private int winDiagonal() {
        int xCount = 0, oCount = 0;
        int numRows = gameModel.getNumberOfRows();

        for (int i = 0; i < numRows; i++) {
            if (X.equals(getLetter(i, i))) {
                xCount++;
            } else if (O.equals(getLetter(i, i))){
                oCount++;
            }
        }
        if (xCount == numRows) {
            return 1;
        } else if (oCount == numRows) {
            return 2;
        }

        xCount = oCount = 0;
        for (int i = numRows - 1; i >= 0 ; i--) {
            if (X.equals(getLetter(i, i))) {
                xCount++;
            } else if (O.equals(getLetter(i, i))){
                oCount++;
            }
        }
        if (xCount == numRows) {
            return 1;
        } else if (oCount == numRows) {
            return 2;
        }

        return 0;
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
