package edu.uob;

import java.util.ArrayList;

public class OXOModel {

    private ArrayList<ArrayList<OXOPlayer>> cells;
    private ArrayList<OXOPlayer> players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;

        cells = new ArrayList<ArrayList<OXOPlayer>>();
        for (int i = 0; i < numberOfRows; i++) {
            ArrayList<OXOPlayer> oxoPlayers = new ArrayList<>();
            for (int j = 0; j < numberOfColumns; j++) {
                oxoPlayers.add(null);
            }
            cells.add(oxoPlayers);
        }

        players = new ArrayList<>();
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public void addPlayer(OXOPlayer player) {
        players.add(player);
    }

    public OXOPlayer getPlayerByNumber(int number) {
        return players.get(number);
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {
        return cells.size();
    }

    public int getNumberOfColumns() {
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        ArrayList<OXOPlayer> oxoPlayers = cells.get(rowNumber);
        oxoPlayers.set(colNumber, player);
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn() {
        gameDrawn = true;
    }

    public void cancelGameDrawn() {
        gameDrawn = false;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

    void addRow() {
        ArrayList<OXOPlayer> oxoPlayers = new ArrayList<>();
        for (int i = 0; i < getNumberOfColumns(); i++) {
            oxoPlayers.add(null);
        }
        cells.add(oxoPlayers);
    }

    void addColumn() {
        for (int i = 0; i < getNumberOfRows(); i++) {
            cells.get(i).add(null);
        }
    }

    void removeRow() {
        cells.remove(getNumberOfRows() - 1);
    }

    void removeColumn() {
        for (int i = 0; i < getNumberOfRows(); i++) {
            cells.get(i).remove(getNumberOfColumns() - 1);
        }
    }
}
