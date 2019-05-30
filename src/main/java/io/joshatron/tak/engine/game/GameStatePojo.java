package io.joshatron.tak.engine.game;

import io.joshatron.tak.engine.board.GameBoard;
import io.joshatron.tak.engine.turn.Turn;
import lombok.Data;

import java.util.List;

@Data
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
}
