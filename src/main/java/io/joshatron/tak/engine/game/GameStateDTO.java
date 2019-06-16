package io.joshatron.tak.engine.game;

import io.joshatron.tak.engine.board.GameBoard;
import io.joshatron.tak.engine.turn.MoveTurn;
import io.joshatron.tak.engine.turn.PlaceTurn;
import io.joshatron.tak.engine.turn.Turn;
import io.joshatron.tak.engine.turn.TurnType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GameStateDTO {
    private int size;
    private int whiteStones;
    private int whiteCapstones;
    private int blackStones;
    private int blackCapstones;
    private Player first;
    private Player current;
    private List<Turn> turns;
    private GameBoard board;

    public GameStateDTO(GameState state) {
        this.size = state.getBoardSize();
        this.whiteStones = state.getWhiteNormalPiecesLeft();
        this.whiteCapstones = state.getWhiteCapstonesLeft();
        this.blackStones = state.getBlackNormalPiecesLeft();
        this.blackCapstones = state.getBlackCapstonesLeft();
        this.first = state.getFirstPlayer();
        this.current = state.getCurrentPlayer();
        this.turns = new ArrayList<>();
        for(Turn turn : state.getTurns()) {
            if(turn.getType() == TurnType.PLACE) {
                turns.add(new PlaceTurn((PlaceTurn)turn));
            }
            else {
                turns.add(new MoveTurn((MoveTurn)turn));
            }
        }
        this.board = new GameBoard(state.getBoard());
    }
}
