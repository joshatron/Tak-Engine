package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.board.grid.GridBoard;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.state.GameState;
import io.joshatron.tak.engine.board.PieceStack;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import lombok.Data;

@Data
public class TakState extends GameState {
    private int size;
    private Player first;
    private Player current;
    private GridBoard board;

    public TakState(Player first, int size) throws BoardGameEngineException {
        super(new TakStatus());

        this.size = size;
        this.first = first;
        this.current = first;

        switch(size) {
            case 3:
                board = new GridBoard(size, size, new PieceStack());
                getPlayers().add(new TakPlayerInfo("WHITE", 10, 0));
                getPlayers().add(new TakPlayerInfo("BLACK", 10, 0));
                break;
            case 4:
                board = new GridBoard(size, size, new PieceStack());
                getPlayers().add(new TakPlayerInfo("WHITE", 15, 0));
                getPlayers().add(new TakPlayerInfo("BLACK", 15, 0));
                break;
            case 5:
                board = new GridBoard(size, size, new PieceStack());
                getPlayers().add(new TakPlayerInfo("WHITE", 21, 1));
                getPlayers().add(new TakPlayerInfo("BLACK", 21, 1));
                break;
            case 6:
                board = new GridBoard(size, size, new PieceStack());
                getPlayers().add(new TakPlayerInfo("WHITE", 30, 1));
                getPlayers().add(new TakPlayerInfo("BLACK", 30, 1));
                break;
            case 8:
                board = new GridBoard(size, size, new PieceStack());
                getPlayers().add(new TakPlayerInfo("WHITE", 50, 2));
                getPlayers().add(new TakPlayerInfo("BLACK", 50, 2));
                break;
            default:
                throw new BoardGameEngineException(TakEngineErrorCode.INVALID_BOARD_SIZE);
        }
    }

    @Override
    public String getDisplayForPlayer(String player) {
        return getPlayers().get(0).toString() + "\n" + getPlayers().get(1).toString() + "\n" + board.toString();
    }
}
