package io.joshatron.tak.engine.turn;

import io.joshatron.bgt.engine.board.grid.GridBoardLocation;
import io.joshatron.bgt.engine.player.PlayerIndicator;
import io.joshatron.bgt.engine.state.Turn;
import io.joshatron.tak.engine.board.PieceType;
import lombok.Data;

@Data
public class TakPlaceTurn extends Turn {
    private final TurnType type = TurnType.PLACE;
    private GridBoardLocation location;
    private PieceType pieceType;

    public TakPlaceTurn(PlayerIndicator player, GridBoardLocation location, PieceType pieceType) {
        super(player);
        this.location = location;
        this.pieceType = pieceType;
    }

    public TakPlaceTurn(PlayerIndicator player, int x, int y, PieceType pieceType) {
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
