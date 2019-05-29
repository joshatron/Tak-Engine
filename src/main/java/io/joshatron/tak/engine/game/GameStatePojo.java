package io.joshatron.tak.engine.game;

import io.joshatron.tak.engine.board.GameBoard;
import io.joshatron.tak.engine.turn.Turn;

import java.util.List;

public class GameStatePojo {
    private int size;
    private int whiteStones;
    private int whiteCapstones;
    private int blackStones;
    private int blackCapstones;
    private Player first;
    private Player current;
    private List<Turn> turns;
    private GameBoard board;

    public GameStatePojo(GameState state) {
        this.size = state.getBoardSize();
        this.whiteStones = state.getWhiteNormalPiecesLeft();
        this.whiteCapstones = state.getWhiteCapstonesLeft();
        this.blackStones = state.getBlackNormalPiecesLeft();
        this.blackCapstones = state.getBlackCapstonesLeft();
        this.first = state.getFirstPlayer();
        this.current = state.getCurrentPlayer();
        this.turns = state.getTurns();
        this.board = state.getBoard();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getWhiteStones() {
        return whiteStones;
    }

    public void setWhiteStones(int whiteStones) {
        this.whiteStones = whiteStones;
    }

    public int getWhiteCapstones() {
        return whiteCapstones;
    }

    public void setWhiteCapstones(int whiteCapstones) {
        this.whiteCapstones = whiteCapstones;
    }

    public int getBlackStones() {
        return blackStones;
    }

    public void setBlackStones(int blackStones) {
        this.blackStones = blackStones;
    }

    public int getBlackCapstones() {
        return blackCapstones;
    }

    public void setBlackCapstones(int blackCapstones) {
        this.blackCapstones = blackCapstones;
    }

    public Player getFirst() {
        return first;
    }

    public void setFirst(Player first) {
        this.first = first;
    }

    public Player getCurrent() {
        return current;
    }

    public void setCurrent(Player current) {
        this.current = current;
    }

    public List<Turn> getTurns() {
        return turns;
    }

    public void setTurns(List<Turn> turns) {
        this.turns = turns;
    }

    public GameBoard getBoard() {
        return board;
    }

    public void setBoard(GameBoard board) {
        this.board = board;
    }
}
