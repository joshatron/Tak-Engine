package io.joshatron.tak.engine.turn;

import io.joshatron.bgt.engine.action.Action;
import io.joshatron.bgt.engine.board.grid.GridBoardLocation;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.player.PlayerIndicator;
import io.joshatron.tak.engine.board.PieceType;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TakPlaceAction extends Action {
    private static final ActionType type = ActionType.PLACE;
    private GridBoardLocation location;
    private PieceType pieceType;

    public TakPlaceAction(PlayerIndicator player, GridBoardLocation location, PieceType pieceType) {
        super(player);
        this.location = location;
        this.pieceType = pieceType;
    }

    public TakPlaceAction(PlayerIndicator player, int x, int y, PieceType pieceType) {
        super(player);
        this.location = new GridBoardLocation(x, y);
        this.pieceType = pieceType;
    }

    //Pattern is "p[piece type] [location]
    public TakPlaceAction(PlayerIndicator player, String action) throws BoardGameEngineException {
        super(player);
        action = action.toLowerCase();
        String[] parts = action.toLowerCase().split(" ");
        if(parts.length != 2) {
            throw new BoardGameEngineException(TakEngineErrorCode.INVALID_TURN_STRING);
        }

        this.pieceType = PieceType.fromString(parts[0].substring(1));
        this.location = new GridBoardLocation(parts[1]);
    }

    @Override
    public String toString() {
        return "p" + pieceType.getAcronym() + " " + location.toString();
    }
}
