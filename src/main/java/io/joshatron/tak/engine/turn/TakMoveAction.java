package io.joshatron.tak.engine.turn;

import io.joshatron.bgt.engine.action.Action;
import io.joshatron.bgt.engine.board.grid.Direction;
import io.joshatron.bgt.engine.board.grid.GridBoardLocation;
import io.joshatron.bgt.engine.exception.BoardGameCommonErrorCode;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.player.PlayerIndicator;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TakMoveAction extends Action {
    private static final ActionType type = ActionType.MOVE;
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

    //Pattern is "m[direction] [start location] g[picked up] [place 1] ... [place n]
    public TakMoveAction(PlayerIndicator player, String action) throws BoardGameEngineException {
        super(player);
        String[] parts = action.toLowerCase().split(" ");

        this.direction = Direction.fromString(parts[0].substring(1));
        if(direction.isDiagonal()) {
            throw new BoardGameEngineException(BoardGameCommonErrorCode.INVALID_DIRECTION);
        }
        this.startLocation = new GridBoardLocation(parts[1]);
        this.pickedUp = Integer.parseInt(parts[2].substring(1));
        this.placed = new int[parts.length - 3];
        this.flattened = false;

        for(int i = 0; i < placed.length; i++) {
            placed[i] = Integer.parseInt(parts[i + 3]);
        }
    }

    public void flatten() {
        flattened = true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("m");
        str.append(direction.getAcronym());
        str.append(" ").append(startLocation.toString());
        str.append(" g").append(pickedUp);

        for(int i = 0; i < placed.length; i++) {
            str.append(" ").append(placed[i]);
        }

        return str.toString();
    }
}
