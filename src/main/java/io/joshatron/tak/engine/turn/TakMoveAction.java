package io.joshatron.tak.engine.turn;

import io.joshatron.bgt.engine.action.Action;
import io.joshatron.bgt.engine.board.grid.Direction;
import io.joshatron.bgt.engine.board.grid.GridBoardLocation;
import io.joshatron.bgt.engine.exception.BoardGameCommonErrorCode;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.player.PlayerIndicator;
import lombok.Data;

@Data
public class TakMoveAction extends Action {
    private final ActionType type = ActionType.MOVE;
    private GridBoardLocation startLocation;
    private int pickedUp;
    private Direction direction;
    private int[] placed;
    private boolean flattened;

    public TakMoveAction(PlayerIndicator player, GridBoardLocation startLocation, int pickedUp, Direction direction, int[] placed) throws BoardGameEngineException {
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

    public TakMoveAction(PlayerIndicator player, int x, int y, int pickedUp, Direction direction, int[] placed) throws BoardGameEngineException {
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
