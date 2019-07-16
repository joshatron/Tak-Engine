package io.joshatron.tak.engine.turn;

import io.joshatron.bgt.engine.board.grid.Direction;
import io.joshatron.bgt.engine.board.grid.GridBoardLocation;
import io.joshatron.bgt.engine.exception.BoardGameCommonErrorCode;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.state.Turn;
import lombok.Data;

@Data
public class TakMoveTurn extends Turn {
    private final TurnType type = TurnType.MOVE;
    private GridBoardLocation startLocation;
    private int pickedUp;
    private Direction direction;
    private int[] placed;
    private boolean flattened;

    public TakMoveTurn(String player, GridBoardLocation startLocation, int pickedUp, Direction direction, int[] placed) throws BoardGameEngineException {
        super(player);
        if(direction.isDiagonal()) {
            throw new BoardGameEngineException(BoardGameCommonErrorCode.INVALID_DIRECTION);
        }
        this.startLocation = startLocation;
        this.pickedUp = pickedUp;
        this.direction = direction;
        this.placed = placed;
        this.flattened = false;
    }

    public TakMoveTurn(String player, int x, int y, int pickedUp, Direction direction, int[] placed) throws BoardGameEngineException {
        super(player);
        if(direction.isDiagonal()) {
            throw new BoardGameEngineException(BoardGameCommonErrorCode.INVALID_DIRECTION);
        }
        this.startLocation = new GridBoardLocation(x, y);
        this.pickedUp = pickedUp;
        this.direction = direction;
        this.placed = placed;
        this.flattened = false;
    }

    public void flatten() {
        flattened = true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("m");
        switch(direction) {
            case NORTH:
                str.append("n");
                break;
            case SOUTH:
                str.append("s");
                break;
            case EAST:
                str.append("e");
                break;
            case WEST:
                str.append("w");
                break;
            default:
                return "";
        }

        str.append(" ").append(startLocation.toString());

        str.append(" g").append(pickedUp);
        for(int i = 0; i < placed.length; i++) {
            str.append(" ").append(placed[i]);
        }

        return str.toString();
    }
}
