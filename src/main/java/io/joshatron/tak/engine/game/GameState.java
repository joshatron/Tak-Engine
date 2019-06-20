package io.joshatron.tak.engine.game;

import io.joshatron.tak.engine.board.GameBoard;
import io.joshatron.tak.engine.engine.GameEngine;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import io.joshatron.tak.engine.exception.TakEngineException;
import io.joshatron.tak.engine.turn.MoveTurn;
import io.joshatron.tak.engine.turn.PlaceTurn;
import io.joshatron.tak.engine.turn.Turn;
import io.joshatron.tak.engine.turn.TurnType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GameState {
    private int size;
    private int whiteStones;
    private int whiteCapstones;
    private int blackStones;
    private int blackCapstones;
    private Player first;
    private Player current;
    private List<Turn> turns;
    private GameBoard board;
    private GameResult result;

    public GameState(Player first, int size) throws TakEngineException {
        this.first = first;
        this.turns = new ArrayList<>();

        result = null;

        this.current = first;

        switch(size) {
            case 3:
                board = new GameBoard(size);
                whiteStones = 10;
                whiteCapstones = 0;
                blackStones = 10;
                blackCapstones = 0;
                break;
            case 4:
                board = new GameBoard(size);
                whiteStones = 15;
                whiteCapstones = 0;
                blackStones = 15;
                blackCapstones = 0;
                break;
            case 5:
                board = new GameBoard(size);
                whiteStones = 21;
                whiteCapstones = 1;
                blackStones = 21;
                blackCapstones = 1;
                break;
            case 6:
                board = new GameBoard(size);
                whiteStones = 30;
                whiteCapstones = 1;
                blackStones = 30;
                blackCapstones = 1;
                break;
            case 8:
                board = new GameBoard(size);
                whiteStones = 50;
                whiteCapstones = 2;
                blackStones = 50;
                blackCapstones = 2;
                break;
            default:
                throw new TakEngineException(TakEngineErrorCode.INVALID_BOARD_SIZE);
        }
    }

    public GameState(GameState state) {
        this.size = state.getSize();
        this.whiteStones = state.getWhiteStones();
        this.whiteCapstones = state.getWhiteCapstones();
        this.blackStones = state.getBlackStones();
        this.blackCapstones = state.getBlackCapstones();
        this.first = state.getFirst();
        this.current = state.getCurrent();
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

    public Turn getLatesTurn() {
        return turns.get(turns.size() - 1);
    }

    public void printBoard() {
        System.out.println("WS: " + whiteStones + " WC: " + whiteCapstones + " BS: " + blackStones + " BC: " + blackCapstones);
        board.printBoard();
    }
}
