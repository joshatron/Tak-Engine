package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.dtos.GameState;
import io.joshatron.bgt.engine.dtos.Turn;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.tak.engine.board.GameBoard;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import io.joshatron.tak.engine.turn.TakMoveTurn;
import io.joshatron.tak.engine.turn.TakPlaceTurn;
import io.joshatron.tak.engine.turn.TakTurn;
import io.joshatron.tak.engine.turn.TurnType;
import lombok.Data;

import java.util.ArrayList;

@Data
public class TakState extends GameState {
    private int size;
    private int whiteStones;
    private int whiteCapstones;
    private int blackStones;
    private int blackCapstones;
    private Player first;
    private Player current;
    private GameBoard board;

    public TakState(Player first, int size) throws BoardGameEngineException {
        super();

        this.first = first;
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
                throw new BoardGameEngineException(TakEngineErrorCode.INVALID_BOARD_SIZE);
        }
    }

    public TakState(TakState state) throws BoardGameEngineException {
        this.size = state.getSize();
        this.whiteStones = state.getWhiteStones();
        this.whiteCapstones = state.getWhiteCapstones();
        this.blackStones = state.getBlackStones();
        this.blackCapstones = state.getBlackCapstones();
        this.first = state.getFirst();
        this.current = state.getCurrent();
        this.turns = new ArrayList<>();
        for(Turn turn : state.getTurns()) {
            if(((TakTurn)turn).getType() == TurnType.PLACE) {
                turns.add(new TakPlaceTurn((TakPlaceTurn)turn));
            }
            else {
                turns.add(new TakMoveTurn((TakMoveTurn)turn));
            }
        }
        this.board = new GameBoard(state.getBoard());
    }

    public void printBoard() {
        System.out.println("WS: " + whiteStones + " WC: " + whiteCapstones + " BS: " + blackStones + " BC: " + blackCapstones);
        board.printBoard();
    }
}
