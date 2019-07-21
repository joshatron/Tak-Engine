package io.joshatron.tak.engine.turn;

import io.joshatron.bgt.engine.board.grid.GridBoardLocation;
import io.joshatron.bgt.engine.player.PlayerIndicator;
import io.joshatron.bgt.engine.turn.Action;
import io.joshatron.tak.engine.board.PieceType;
import lombok.Data;

@Data
public class TakPlaceAction extends Action {
    private final ActionType type = ActionType.PLACE;
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

    @Override
    public String toString() {
        String str = "p";
        switch(pieceType) {
            case STONE:
                str += "s";
                break;
            case WALL:
                str += "w";
                break;
            case CAPSTONE:
                str += "c";
                break;
        }

        str += " ";
        str += location.toString();

        return str;
    }
}
