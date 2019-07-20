package io.joshatron.tak.engine.turn;

import io.joshatron.bgt.engine.board.grid.GridBoardLocation;
import io.joshatron.bgt.engine.state.Turn;
import io.joshatron.tak.engine.board.PieceType;
import io.joshatron.tak.engine.game.TakState;
import lombok.Data;

@Data
public class TakPlaceTurn extends Turn {
    private final TurnType type = TurnType.PLACE;
    private GridBoardLocation location;
    private PieceType pieceType;

    public TakPlaceTurn(String player, GridBoardLocation location, PieceType pieceType) {
        super(player);
        this.location = location;
        this.pieceType = pieceType;
    }

    public TakPlaceTurn(String player, int x, int y, PieceType pieceType) {
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
